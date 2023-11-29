/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2000-2022 by Andre Winkler. All
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

package de.betoffice.web;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.betoffice.web.json.GameJson;
import de.betoffice.web.json.GroupTeamTableJson;
import de.betoffice.web.json.GroupTypeJson;
import de.betoffice.web.json.IGameJson;
import de.betoffice.web.json.PingJson;
import de.betoffice.web.json.RoundAndTableJson;
import de.betoffice.web.json.RoundJson;
import de.betoffice.web.json.SeasonJson;
import de.betoffice.web.json.SubmitTippGameJson;
import de.betoffice.web.json.SubmitTippRoundJson;
import de.betoffice.web.json.TeamJson;
import de.betoffice.web.json.TeamResultJson;
import de.betoffice.web.json.UserJson;
import de.betoffice.web.json.UserTableJson;
import de.winkler.betoffice.service.AuthService;
import de.winkler.betoffice.service.CommunityCalculatorService;
import de.winkler.betoffice.service.CommunityService;
import de.winkler.betoffice.service.DateTimeProvider;
import de.winkler.betoffice.service.MasterDataManagerService;
import de.winkler.betoffice.service.SeasonManagerService;
import de.winkler.betoffice.service.TippService;
import de.winkler.betoffice.storage.Game;
import de.winkler.betoffice.storage.GameList;
import de.winkler.betoffice.storage.GameTipp;
import de.winkler.betoffice.storage.Group;
import de.winkler.betoffice.storage.GroupType;
import de.winkler.betoffice.storage.Nickname;
import de.winkler.betoffice.storage.Season;
import de.winkler.betoffice.storage.SeasonRange;
import de.winkler.betoffice.storage.Session;
import de.winkler.betoffice.storage.Team;
import de.winkler.betoffice.storage.TeamResult;
import de.winkler.betoffice.storage.TippDto;
import de.winkler.betoffice.storage.TippDto.GameTippDto;
import de.winkler.betoffice.storage.User;
import de.winkler.betoffice.storage.UserResult;

/**
 * Basic rest service features for betoffice.
 * 
 * TODO Hier gibt es die ein oder andere Codestelle, die vielleicht einmal ueberprueft werden sollte.
 *
 * @author Andre Winkler
 */
@Component("betofficeBasicService")
public class DefaultBetofficeService implements BetofficeService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultBetofficeService.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private DateTimeProvider dateTimeProvider;

    @Autowired
    private SeasonManagerService seasonManagerService;

    @Autowired
    private MasterDataManagerService masterDataManagerService;

    @Autowired
    private TippService tippService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private CommunityCalculatorService communityCalculatorService;

    // ------------------------------------------------------------------------

    @Override
    public SeasonJson findSeasonById(Long seasonId) {
        Season season = seasonManagerService.findSeasonById(seasonId);
        List<GameList> rounds = seasonManagerService.findRounds(season);

        Optional<GameList> nextTippRound = tippService.findNextTippRound(seasonId, dateTimeProvider.currentDateTime());

        JsonAssembler jsonAssembler = new JsonAssembler();
        SeasonJson seasonJson = jsonAssembler
                .build(season)
                .rounds(rounds)
                .currentRound(nextTippRound)
                .assemble();

        return seasonJson;
    }

    @Override
    public List<GroupTypeJson> findAllGroups(Long seasonId) {
        Season season = seasonManagerService.findSeasonById(seasonId);
        List<GroupType> groupTypes = seasonManagerService.findGroupTypesBySeason(season);

        return JsonBuilder.toJsonWithGroupTypes(groupTypes);
    }

    @Override
    public List<RoundJson> findAllRounds(Long seasonId) {
        Season season = seasonManagerService.findSeasonById(seasonId);
        List<GameList> rounds = seasonManagerService.findRounds(season);
        return JsonBuilder.toJsonWithGameList(rounds);
    }

    @Override
    public SeasonJson findAllRounds(Long seasonId, Long groupTypeId) {
        Season season = seasonManagerService.findSeasonById(seasonId);
        GroupType groupType = masterDataManagerService.findGroupType(groupTypeId);
        Group group = seasonManagerService.findGroup(season, groupType);
        List<GameList> rounds = seasonManagerService.findRounds(group);

        Optional<GameList> nextTippRound = tippService.findNextTippRound(seasonId, dateTimeProvider.currentDateTime());

        JsonAssembler jsonAssembler = new JsonAssembler();
        SeasonJson seasonJson = jsonAssembler
                .build(season)
                .rounds(rounds)
                .currentRound(nextTippRound)
                .assemble();

        return seasonJson;
    }

    @Override
    public RoundJson findRound(Long roundId) {
        GameList gameList = seasonManagerService.findRound(roundId);
        RoundJson roundJson = null;

        if (gameList != null) {
            Optional<GameList> nextRound = seasonManagerService.findNextRound(roundId);

            JsonAssembler jsonAssembler = new JsonAssembler();
            roundJson = jsonAssembler.build(gameList).games().lastRound(!nextRound.isPresent()).assemble();
        }

        return roundJson;
    }

    @Override
    public RoundJson findRound(Long roundId, Long groupTypeId) {
        Optional<GameList> gameList = seasonManagerService.findRoundGames(roundId);
        GroupType groupType = masterDataManagerService.findGroupType(groupTypeId);
        Group group = seasonManagerService.findGroup(gameList.get().getSeason(), groupType);

        RoundJson roundJson = null;

        if (gameList != null) {
            Optional<GameList> nextRound = seasonManagerService.findNextRound(roundId);

            JsonAssembler jsonAssembler = new JsonAssembler();
            roundJson = jsonAssembler.build(gameList.get()).games(gameList.get().toList(group))
                    .lastRound(!nextRound.isPresent()).assemble();
        }

        return roundJson;
    }

    @Override
    public RoundJson findNextRound(Long roundId) {
        Optional<GameList> nextRound = seasonManagerService.findNextRound(roundId);
        RoundJson roundJson = null;

        if (nextRound.isPresent()) {
            Optional<GameList> nextNextRound = seasonManagerService.findNextRound(nextRound.get().getId());

            JsonAssembler jsonAssembler = new JsonAssembler();
            roundJson = jsonAssembler.build(nextRound.get()).lastRound(!nextNextRound.isPresent()).games().assemble();
        }

        return roundJson;
    }

    @Override
    public RoundJson findPrevRound(Long roundId) {
        Optional<GameList> prevRound = seasonManagerService.findPrevRound(roundId);
        RoundJson roundJson = null;

        if (prevRound.isPresent()) {
            JsonAssembler jsonAssembler = new JsonAssembler();
            roundJson = jsonAssembler.build(prevRound.get()).lastRound(false).games().assemble();
        }

        return roundJson;
    }

    @Override
    public RoundAndTableJson findRoundTable(Long roundId, Long groupTypeId) {
        RoundJson roundJson = findRound(roundId, groupTypeId);

        Season season = seasonManagerService.findSeasonById(roundJson.getSeasonId());
        GroupType groupType = masterDataManagerService.findGroupType(groupTypeId);

        RoundAndTableJson roundAndTableJson = new RoundAndTableJson();
        roundAndTableJson.setRoundJson(roundJson);

        List<TeamResult> teamRanking = seasonManagerService.calculateTeamRanking(season, groupType, 0 /* startIndex */,
                roundJson.getIndex() - 1);

        GroupTeamTableJson groupTeamTableJson = new GroupTeamTableJson();
        groupTeamTableJson.setGroupTypeJson(JsonBuilder.toJson(groupType));

        for (TeamResult teamResult : teamRanking) {
            TeamResultJson teamResultJson = JsonBuilder.toJson(teamResult);
            groupTeamTableJson.add(teamResultJson);
        }

        roundAndTableJson.setGroupTeamTableJson(groupTeamTableJson);

        return roundAndTableJson;
    }

    @Override
    public RoundAndTableJson findNextRoundTable(Long roundId) {
        return findRoundTable(roundId, null);
        // TODO Auto-generated method stub
        // return null;
    }

    @Override
    public RoundAndTableJson findPrevRoundTable(Long roundId) {
        return findRoundTable(roundId, null);
        // TODO Auto-generated method stub
        // return null;
    }

    @Override
    public IGameJson findGame(Long gameId) {
        Game game = seasonManagerService.findMatch(gameId);
        return JsonBuilder.toJson(game);
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
    public Optional<RoundJson> findCurrent(Long seasonId) {
        return tippService
                .findNextTippRound(seasonId, dateTimeProvider.currentDateTime())
                .map(i -> JsonBuilder.toJson(i));
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

    @Override
    public UserTableJson calcUserRanking(Long seasonId) {
        Season season = seasonManagerService.findSeasonById(seasonId);
        Optional<GameList> round = tippService.findPreviousTippRound(seasonId, dateTimeProvider.currentDateTime());

        // TODO Ist das vielleicht besser ein Optional hier?
        // #findPreviousTippRound
        // liefert dann null, wenn die Meisterschaft noch nicht gestartet ist.

        UserTableJson userTableJson = new UserTableJson();
        if (round.isEmpty()) {
            // Dann gibt es keine Tipprunde und es kann der letzte Spieltag
            // angenommen werden.
            // TODO: Was ist besser? Der erste oder der letzte Spieltag?
            // Falls die Saison noch nicht gestartet ist, dann ist der erste
            // Spieltag eine gute Loesung.
            // Falls die Saison vorbei ist, ist der letzte Spieltag die bessere
            // Wahl.

            // TODO Saison noch ohne ersten Spieltag.
            Optional<GameList> lastRound = seasonManagerService.findFirstRound(season);

            // TODO ... fix me
            round = lastRound;
        }

        userTableJson.setRound(JsonBuilder.toJson(round.get()));
        return calcUserRanking(userTableJson, round.get(), 0);
    }

    @Override
    public UserTableJson calcUserRankingByRoundOnly(Long roundId) {
        // GameList round = seasonManagerService.findRound(roundId);
        // return calcUserRanking(round, round.getIndex());

        Optional<GameList> round = seasonManagerService.findRoundGames(roundId);
        if (round.isPresent()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Calculate user ranking for a single round: {}", round.get().getDateTime());
                for (Game game : round.get().unmodifiableList()) {
                    LOG.debug("Game: {}", game.debug());
                }
            }

            List<GameTipp> tipps = tippService.findTipps(roundId);

            if (LOG.isDebugEnabled()) {
                for (GameTipp tipp : tipps) {
                    LOG.debug("GameTipp: {}", tipp.debug());
                }
            }

            UserTableJson userTableJson = new UserTableJson();
            userTableJson.setRound(JsonBuilder.toJson(round.get()));

            // userTableJson.setRound(JsonBuilder.toJsonWithGames(round));
            List<GameJson> jsonWithGamesAndTipps = JsonBuilder.toJsonWithGamesAndTipps(round.get().unmodifiableList(),
                    tipps);
            userTableJson.getRound().setGames(jsonWithGamesAndTipps);

            return calcUserRanking(userTableJson, round.get(), round.get().getIndex());
        } else {
            return null;
        }
    }

    @Override
    public UserTableJson calcUserRankingByRound(Long roundId) {
        Optional<GameList> round = seasonManagerService.findRoundGames(roundId);

        UserTableJson userTableJson = new UserTableJson();
        userTableJson.setRound(JsonBuilder.toJson(round.get()));

        List<GameTipp> tipps = tippService.findTipps(roundId);

        // userTableJson.setRound(JsonBuilder.toJsonWithGames(round));
        List<GameJson> jsonWithGamesAndTipps = JsonBuilder.toJsonWithGamesAndTipps(round.get().unmodifiableList(),
                tipps);
        userTableJson.getRound().setGames(jsonWithGamesAndTipps);

        return calcUserRanking(userTableJson, round.get(), 0);
    }

    private UserTableJson calcUserRanking(UserTableJson userTableJson, GameList round, int startIndex) {
        Season season = seasonManagerService.findSeasonById(round.getSeason().getId());
        List<UserResult> calculatedRanking = communityCalculatorService.calculateRanking(
                CommunityService.defaultPlayerGroup(season.getReference()),
                SeasonRange.of(startIndex, round.getIndex()));

        for (UserResult ur : calculatedRanking) {
            UserJson userJson = JsonBuilder.toJson(ur);
            userTableJson.addUser(userJson);
        }

        SeasonJson seasonJson = JsonBuilder.toJson(round.getSeason());
        userTableJson.setSeason(seasonJson);

        findNextAndPrevRound(round, userTableJson);

        return userTableJson;
    }

    private void findNextAndPrevRound(GameList round, UserTableJson userTableJson) {

        Optional<GameList> nextNextRound = seasonManagerService.findNextRound(round.getId());
        userTableJson.getRound().setLastRound(!nextNextRound.isPresent());
        userTableJson.getRound().setTippable(isFinished(userTableJson.getRound()));
    }

    @Override
    public UserTableJson calcUserRankingByNextRound(Long roundId) {
        Optional<GameList> nextRound = seasonManagerService.findNextRound(roundId);
        if (nextRound.isPresent()) {
            return calcUserRankingByRound(nextRound.get().getId());
        }
        return null;
    }

    @Override
    public UserTableJson calcUserRankingByPrevRound(Long roundId) {
        Optional<GameList> prevRound = seasonManagerService.findPrevRound(roundId);
        if (prevRound.isPresent()) {
            return calcUserRankingByRound(prevRound.get().getId());
        }
        return null;
    }

    @Override
    public List<TeamJson> findAllTeams() {
        List<Team> teams = masterDataManagerService.findAllTeams();
        return JsonBuilder.toJsonWithTeams(teams);
    }

    @Override
    public List<SeasonJson> findAllSeason() {
        List<Season> seasons = seasonManagerService.findAllSeasons();
        return JsonBuilder.toJsonWithSeasons(seasons);
    }

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

    private boolean isFinished(RoundJson round) {
        boolean finished = false;
        for (IGameJson game : round.getGames()) {
            if (!game.isFinished()) {
                finished = true;
            }
        }
        return finished;
    }

    @Override
    public PingJson ping() {
        PingJson pingJson = new PingJson();
        pingJson.setDateTime(dateTimeProvider.currentDateTime());
        return pingJson;
    }

}
