/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2000-2025 by Andre Winkler. All
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.betoffice.service.CommunityService;
import de.betoffice.service.SeasonManagerService;
import de.betoffice.service.TippService;
import de.betoffice.storage.season.GameDto;
import de.betoffice.storage.season.RoundDto;
import de.betoffice.storage.season.entity.GameListEntity;
import de.betoffice.storage.season.entity.DtoAssembler;
import de.betoffice.storage.season.entity.DtoBuilder;
import de.betoffice.storage.time.DateTimeProvider;
import de.betoffice.storage.tip.GameTippEntity;
import de.betoffice.storage.tip.TippDto;
import de.betoffice.storage.tip.TippDto.GameTippDto;
import de.betoffice.storage.user.entity.Nickname;
import de.betoffice.storage.user.entity.UserEntity;

@Service
@Transactional(readOnly = true)
public class DefaultOfficeTippService implements OfficeTippService {

    @Autowired
    private DateTimeProvider dateTimeProvider;

    @Autowired
    private SeasonManagerService seasonManagerService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private TippService tippService;

    @PreAuthorize("@tippAuthorisationService.isSubmissionAllowed(#token, #tippRoundJson.nickname)")
    @Override
    @Transactional
    public RoundDto submitTipp(String token, SubmitTippRoundJson tippRoundJson) {
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
        List<GameTippEntity> tipps = tippService.validateKickOffTimeAndAddTipp(tippDto);

        return findTipp(tippRoundJson.getRoundId(), tippRoundJson.getNickname());
    }

    @Override
    public RoundDto findTipp(Long roundId, String nickName) {
        Optional<UserEntity> user = communityService.findUser(Nickname.of(nickName));

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
        RoundDto roundJson = null;
        Optional<GameListEntity> round = seasonManagerService.findRoundGames(roundId);
        if (round.isPresent()) {
            List<GameTippEntity> roundTipps = tippService.findTipps(round.get(), user.get());
            Optional<GameListEntity> nextNextRound = seasonManagerService.findNextRound(roundId);

            DtoAssembler jsonAssembler = new DtoAssembler();

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
    public Optional<RoundDto> findCurrentTipp(Long seasonId, String nickName) {
        ZonedDateTime currentDateTime = dateTimeProvider.currentDateTime();
        return tippService
                .findNextTippRound(seasonId, currentDateTime)
                .map(i -> findTipp(i.getId(), nickName));
    }

    @Override
    public Optional<RoundDto> findNextTipp(Long roundId, String nickName) {
        return seasonManagerService
                .findNextRound(roundId)
                .map(i -> findTipp(i.getId(), nickName));
    }

    @Override
    public Optional<RoundDto> findPrevTipp(Long roundId, String nickName) {
        return seasonManagerService
                .findPrevRound(roundId)
                .map(i -> findTipp(i.getId(), nickName));
    }

    @Override
    public Optional<RoundDto> findTippRound(Long seasonId) {
        return tippService.findNextTippRound(seasonId, dateTimeProvider.currentDateTime())
                .map(gameList -> {
                    RoundDto roundJson = DtoBuilder.toJson(gameList);
                    List<GameDto> gameJson = DtoBuilder.toJsonWithGames(gameList.unmodifiableList());
                    roundJson.getGames().addAll(gameJson);
                    roundJson.setTippable(isFinished(roundJson));
                    return roundJson;
                });
    }

    private boolean isFinished(RoundDto round) {
        boolean finished = false;
        for (GameDto game : round.getGames()) {
            if (!game.isFinished()) {
                finished = true;
            }
        }
        return finished;
    }

}
