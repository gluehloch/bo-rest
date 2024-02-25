/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2000-2024 by Andre Winkler. All
 * rights reserved.
 * ============================================================================
 * GNU GENERAL PUBLIC LICENSE TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND
 * MODIFICATION
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package de.betoffice.web.tipp;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.betoffice.web.AccessDeniedException;
import de.betoffice.web.json.GameJson;
import de.betoffice.web.json.IGameJson;
import de.betoffice.web.json.JsonAssembler;
import de.betoffice.web.json.JsonBuilder;
import de.betoffice.web.json.RoundJson;
import de.betoffice.web.json.SubmitTippGameJson;
import de.betoffice.web.json.SubmitTippRoundJson;
import de.winkler.betoffice.service.AuthService;
import de.winkler.betoffice.service.CommunityService;
import de.winkler.betoffice.service.DateTimeProvider;
import de.winkler.betoffice.service.SeasonManagerService;
import de.winkler.betoffice.service.TippService;
import de.winkler.betoffice.storage.GameList;
import de.winkler.betoffice.storage.GameTipp;
import de.winkler.betoffice.storage.Nickname;
import de.winkler.betoffice.storage.Session;
import de.winkler.betoffice.storage.TippDto;
import de.winkler.betoffice.storage.TippDto.GameTippDto;
import de.winkler.betoffice.storage.User;

@Service
public class DefaultOfficeTippService implements OfficeTippService {

    @Autowired
    private DateTimeProvider dateTimeProvider;

    @Autowired
    private SeasonManagerService seasonManagerService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private TippService tippService;

    @Autowired
    private AuthService authService;

    @Override
    public RoundJson submitTipp(String token, SubmitTippRoundJson tippRoundJson) throws AccessDeniedException {
        Session session = authService.validateSession(token).orElseThrow(() -> new AccessDeniedException());

        if (!StringUtils.equals(session.getUser().getNickname().value(), tippRoundJson.getNickname())) {
            throw new AccessDeniedException();
        }

        TippDto tippDto = new TippDto();
        tippDto.setNickname(tippRoundJson.getNickname());
        tippDto.setRoundId(tippRoundJson.getRoundId());
        tippDto.setToken(token);
        tippDto.setSubmitTime(dateTimeProvider.currentDateTime());

        for (SubmitTippGameJson submitTippJson : tippRoundJson.getSubmitTippGames()) {
            GameTippDto gameTippDto = new GameTippDto();
            gameTippDto.setGameId(submitTippJson.getGameId());
            gameTippDto.setHomeGoals(submitTippJson.getTippResult().getHomeGoals());
            gameTippDto.setGuestGoals(submitTippJson.getTippResult().getGuestGoals());
            tippDto.addGameTipp(gameTippDto);
        }

        //
        // TODO ...
        // Falls nach Spielbeginn abgegeben, kommt hier nur eine Teilmenge der Tipps
        // zurueck.
        //
        List<GameTipp> tipps = tippService.validateKickOffTimeAndAddTipp(tippDto);

        return findTipp(tippRoundJson.getRoundId(), tippRoundJson.getNickname());
    }

    @Override
    public RoundJson findTipp(Long roundId, String nickName) {
        Optional<User> user = communityService.findUser(Nickname.of(nickName));

        if (!user.isPresent()) {
            return null;
        }

        // HINWEIS: Diese Stelle ist problematisch. Es wird nach allen Tipps
        // zu diesem Spieltag gesucht. Falls für ein Spiel ein Tipp fehlt,
        // so steht dieses als NULL in der Spieltagsliste.
        //
        // Die folgende Methode wuerde das Problem verursachen;
        // GameList tippRound = tippService.findTipp(roundId.longValue(),
        // user.get().getId().longValue());
        //
        RoundJson roundJson = null;
        Optional<GameList> round = seasonManagerService.findRoundGames(roundId);
        if (round.isPresent()) {
            List<GameTipp> roundTipps = tippService.findTipps(round.get(), user.get());
            Optional<GameList> nextNextRound = seasonManagerService.findNextRound(roundId);

            JsonAssembler jsonAssembler = new JsonAssembler();

            if (roundTipps.isEmpty()) {
                roundJson = jsonAssembler.build(round.get())
                        .lastRound(!nextNextRound.isPresent())
                        .games()
                        .emptyTipp()
                        .assemble();
            } else {
                roundJson = jsonAssembler.build(round.get())
                        .lastRound(!nextNextRound.isPresent())
                        .games()
                        .tipps(roundTipps)
                        .assemble();
            }
        }

        return roundJson;
    }

    @Override
    public Optional<RoundJson> findCurrentTipp(Long seasonId, String nickName) {
        ZonedDateTime currentDateTime = dateTimeProvider.currentDateTime();
        return tippService
                .findNextTippRound(seasonId, currentDateTime)
                .map(i -> findTipp(i.getId(), nickName));
    }

    @Override
    public Optional<RoundJson> findNextTipp(Long roundId, String nickName) {
        return seasonManagerService
                .findNextRound(roundId)
                .map(i -> findTipp(i.getId(), nickName));
    }

    @Override
    public Optional<RoundJson> findPrevTipp(Long roundId, String nickName) {
        return seasonManagerService
                .findPrevRound(roundId)
                .map(i -> findTipp(i.getId(), nickName));
    }

    @Override
    public Optional<RoundJson> findTippRound(Long seasonId) {
        return tippService.findNextTippRound(seasonId, dateTimeProvider.currentDateTime())
                .map(gameList -> {
                    RoundJson roundJson = JsonBuilder.toJson(gameList);
                    List<GameJson> gameJson = JsonBuilder.toJsonWithGames(gameList.unmodifiableList());
                    roundJson.getGames().addAll(gameJson);
                    roundJson.setTippable(isFinished(roundJson));
                    return roundJson;
                });
    }

    private boolean isFinished(RoundJson round) {
        boolean finished = false;
        for (IGameJson game : round.getGames()) {
            if (!game.isFinished()) {
                finished = true;
            }
        }
        return finished;
    }

}
