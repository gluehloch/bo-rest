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

package de.betoffice.web.season;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.betoffice.service.CommunityCalculatorService;
import de.betoffice.service.CommunityService;
import de.betoffice.service.MasterDataManagerService;
import de.betoffice.service.SeasonManagerService;
import de.betoffice.service.TippService;
import de.betoffice.storage.group.GroupTypeDto;
import de.betoffice.storage.group.entity.GroupTeamTableDto;
import de.betoffice.storage.group.entity.GroupTypeEntity;
import de.betoffice.storage.season.GameDto;
import de.betoffice.storage.season.RoundAndTableDto;
import de.betoffice.storage.season.RoundDto;
import de.betoffice.storage.season.SeasonDto;
import de.betoffice.storage.season.SeasonRange;
import de.betoffice.storage.season.TeamResultDto;
import de.betoffice.storage.season.UserRankingDto;
import de.betoffice.storage.season.UserRankingTableDto;
import de.betoffice.storage.season.entity.GameEntity;
import de.betoffice.storage.season.entity.GameListEntity;
import de.betoffice.storage.season.entity.GoalEntity;
import de.betoffice.storage.season.entity.GoalDtoMapper;
import de.betoffice.storage.season.entity.GroupEntity;
import de.betoffice.storage.season.entity.DtoAssembler;
import de.betoffice.storage.season.entity.DtoBuilder;
import de.betoffice.storage.season.entity.SeasonEntity;
import de.betoffice.storage.team.TeamDto;
import de.betoffice.storage.team.TeamResult;
import de.betoffice.storage.team.TeamType;
import de.betoffice.storage.team.entity.TeamEntity;
import de.betoffice.storage.team.entity.TeamDtoMapper;
import de.betoffice.storage.time.DateTimeProvider;
import de.betoffice.storage.tip.GameTippEntity;
import de.betoffice.storage.user.UserResult;
import de.betoffice.web.json.PingJson;

/**
 * Basic rest service features for betoffice.
 * 
 * TODO Hier gibt es die ein oder andere Codestelle, die vielleicht einmal ueberprueft werden sollte.
 *
 * @author Andre Winkler
 */
@Component("betofficeBasicService")
@Transactional(readOnly = true)
public class DefaultBetofficeService implements BetofficeService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultBetofficeService.class);

    @Autowired
    private DateTimeProvider dateTimeProvider;

    @Autowired
    private SeasonManagerService seasonManagerService;

    @Autowired
    private MasterDataManagerService masterDataManagerService;

    @Autowired
    private TippService tippService;

    @Autowired
    private CommunityCalculatorService communityCalculatorService;

    // ------------------------------------------------------------------------

    @Override
    public SeasonDto findSeasonById(Long seasonId) {
        SeasonEntity season = seasonManagerService.findSeasonById(seasonId);
        List<GameListEntity> rounds = seasonManagerService.findRounds(season);

        Optional<GameListEntity> nextTippRound = tippService.findNextTippRound(seasonId,
                dateTimeProvider.currentDateTime());

        DtoAssembler jsonAssembler = new DtoAssembler();
        SeasonDto seasonJson = jsonAssembler
                .build(season)
                .rounds(rounds)
                .currentRound(nextTippRound)
                .assemble();

        return seasonJson;
    }

    @Override
    public List<GroupTypeDto> findAllGroups(Long seasonId) {
        SeasonEntity season = seasonManagerService.findSeasonById(seasonId);
        List<GroupTypeEntity> groupTypes = seasonManagerService.findGroupTypes(season);

        return DtoBuilder.toJsonWithGroupTypes(groupTypes);
    }

    @Override
    public List<RoundDto> findAllRounds(Long seasonId) {
        SeasonEntity season = seasonManagerService.findSeasonById(seasonId);
        List<GameListEntity> rounds = seasonManagerService.findRounds(season);
        return DtoBuilder.toJsonWithGameList(rounds);
    }

    @Override
    public SeasonDto findAllRounds(Long seasonId, Long groupTypeId) {
        SeasonEntity season = seasonManagerService.findSeasonById(seasonId);
        GroupTypeEntity groupType = masterDataManagerService.findGroupType(groupTypeId);
        GroupEntity group = seasonManagerService.findGroup(season, groupType);
        List<GameListEntity> rounds = seasonManagerService.findRounds(group);

        Optional<GameListEntity> nextTippRound = tippService.findNextTippRound(seasonId,
                dateTimeProvider.currentDateTime());

        DtoAssembler jsonAssembler = new DtoAssembler();
        SeasonDto seasonJson = jsonAssembler
                .build(season)
                .rounds(rounds)
                .currentRound(nextTippRound)
                .assemble();

        return seasonJson;
    }

    @Override
    public RoundDto findRound(Long seasonId, Long roundId) {
        SeasonEntity season = seasonManagerService.findSeasonById(seasonId);
        GameListEntity gameList = seasonManagerService.findRound(roundId);
        RoundDto roundJson = null;

        if (gameList == null) {
            Optional<GameListEntity> firstRound = seasonManagerService.findFirstRound(season);

            DtoAssembler jsonAssembler = new DtoAssembler();
            roundJson = jsonAssembler.build(firstRound.get()).games().lastRound(true).assemble();
        } else {
            Optional<GameListEntity> nextRound = seasonManagerService.findNextRound(roundId);

            DtoAssembler jsonAssembler = new DtoAssembler();
            roundJson = jsonAssembler.build(gameList).games().lastRound(!nextRound.isPresent()).assemble();
        }

        return roundJson;
    }

    @Override
    public RoundDto findRoundByGroup(Long seasonId, Long roundId, Long groupTypeId) {
        SeasonEntity season = seasonManagerService.findSeasonById(seasonId);
        GameListEntity gameList = seasonManagerService.findRoundGames(roundId)
                .orElseGet(() -> seasonManagerService.findFirstRound(season).orElseThrow());
        GroupTypeEntity groupType = masterDataManagerService.findGroupType(groupTypeId);
        GroupEntity group = seasonManagerService.findGroup(season, groupType);

        RoundDto roundJson = null;

        if (gameList != null) {
            Optional<GameListEntity> nextRound = seasonManagerService.findNextRound(roundId);

            DtoAssembler jsonAssembler = new DtoAssembler();
            roundJson = jsonAssembler.build(gameList)
                    .games(gameList.toList(group))
                    .lastRound(!nextRound.isPresent())
                    .assemble();
        }

        return roundJson;
    }

    @Override
    public RoundDto findNextRound(Long seasonId, Long roundId) {
        Optional<GameListEntity> nextRound = seasonManagerService.findNextRound(roundId);
        RoundDto roundJson = null;

        if (nextRound.isPresent()) {
            Optional<GameListEntity> nextNextRound = seasonManagerService.findNextRound(nextRound.get().getId());

            DtoAssembler jsonAssembler = new DtoAssembler();
            roundJson = jsonAssembler.build(nextRound.get()).lastRound(!nextNextRound.isPresent()).games().assemble();
        }

        return roundJson;
    }

    @Override
    public RoundDto findPrevRound(Long seasonId, Long roundId) {
        Optional<GameListEntity> prevRound = seasonManagerService.findPrevRound(roundId);
        RoundDto roundJson = null;

        if (prevRound.isPresent()) {
            DtoAssembler jsonAssembler = new DtoAssembler();
            roundJson = jsonAssembler.build(prevRound.get()).lastRound(false).games().assemble();
        }

        return roundJson;
    }

    @Override
    public RoundAndTableDto findRoundTable(Long seasonId, Long roundId, Long groupTypeId) {
        RoundDto roundJson = findRoundByGroup(seasonId, roundId, groupTypeId);
        SeasonEntity season = seasonManagerService.findSeasonById(seasonId);
        GroupTypeEntity groupType = masterDataManagerService.findGroupType(groupTypeId);

        RoundAndTableDto roundAndTableJson = new RoundAndTableDto();
        roundAndTableJson.setRoundJson(roundJson);

        List<TeamResult> teamRanking = seasonManagerService.calculateTeamRanking(season, groupType, 0 /* startIndex */,
                roundJson.getIndex() - 1);

        GroupTeamTableDto groupTeamTableJson = new GroupTeamTableDto();
        groupTeamTableJson.setGroupTypeJson(DtoBuilder.toJson(groupType));

        for (TeamResult teamResult : teamRanking) {
            TeamResultDto teamResultJson = DtoBuilder.toJson(teamResult);
            groupTeamTableJson.add(teamResultJson);
        }

        roundAndTableJson.setGroupTeamTableJson(groupTeamTableJson);

        return roundAndTableJson;
    }

    @Override
    public RoundAndTableDto findNextRoundTable(Long seasonId, Long roundId) {
        return findRoundTable(seasonId, roundId, null);
        // TODO Auto-generated method stub
        // return null;
    }

    @Override
    public RoundAndTableDto findPrevRoundTable(Long seasonId, Long roundId) {
        return findRoundTable(seasonId, roundId, null);
        // TODO Auto-generated method stub
        // return null;
    }

    @Override
    public GameDto findGame(Long gameId) {
        GameEntity game = seasonManagerService.findMatch(gameId);
        return DtoBuilder.toJson(game);
    }

    @Override
    public GameDto findDetailGame(Long gameId) {
        GameEntity game = seasonManagerService.findMatch(gameId);
        List<GoalEntity> goals = seasonManagerService.findGoalsOfMatch(game);
        GameDto json = DtoBuilder.toJson(game);
        json.setGoals(GoalDtoMapper.map(goals));
        return json;
    }

    @Override
    public Optional<RoundDto> findCurrent(Long seasonId) {
        return tippService
                .findNextTippRound(seasonId, dateTimeProvider.currentDateTime())
                .map(i -> DtoBuilder.toJson(i));
    }

    @Override
    public UserRankingTableDto calcUserRanking(Long seasonId) {
        SeasonEntity season = seasonManagerService.findSeasonById(seasonId);
        Optional<GameListEntity> round = tippService.findPreviousTippRound(seasonId,
                dateTimeProvider.currentDateTime());

        // TODO Ist das vielleicht besser ein Optional hier?
        // #findPreviousTippRound
        // liefert dann null, wenn die Meisterschaft noch nicht gestartet ist.

        UserRankingTableDto userTableJson = new UserRankingTableDto();
        if (round.isEmpty()) {
            // Dann gibt es keine Tipprunde und es kann der letzte Spieltag
            // angenommen werden.
            // TODO: Was ist besser? Der erste oder der letzte Spieltag?
            // Falls die Saison noch nicht gestartet ist, dann ist der erste
            // Spieltag eine gute Loesung.
            // Falls die Saison vorbei ist, ist der letzte Spieltag die bessere
            // Wahl.

            // TODO Saison noch ohne ersten Spieltag.
            Optional<GameListEntity> lastRound = seasonManagerService.findFirstRound(season);

            // TODO ... fix me
            round = lastRound;
        }

        userTableJson.setRound(DtoBuilder.toJson(round.get()));
        return calcUserRanking(userTableJson, round.get(), 0);
    }

    @Override
    public UserRankingTableDto calcUserRankingByRoundOnly(Long roundId) {
        // GameList round = seasonManagerService.findRound(roundId);
        // return calcUserRanking(round, round.getIndex());

        Optional<GameListEntity> round = seasonManagerService.findRoundGames(roundId);
        if (round.isPresent()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Calculate user ranking for a single round: {}", round.get().getDateTime());
                for (GameEntity game : round.get().unmodifiableList()) {
                    LOG.debug("Game: {}", game.debug());
                }
            }

            List<GameTippEntity> tipps = tippService.findTipps(roundId);

            if (LOG.isDebugEnabled()) {
                for (GameTippEntity tipp : tipps) {
                    LOG.debug("GameTipp: {}", tipp.debug());
                }
            }

            UserRankingTableDto userTableJson = new UserRankingTableDto();
            userTableJson.setRound(DtoBuilder.toJson(round.get()));

            // userTableJson.setRound(JsonBuilder.toJsonWithGames(round));
            List<GameDto> jsonWithGamesAndTipps = DtoBuilder.toJsonWithGamesAndTipps(round.get().unmodifiableList(),
                    tipps);
            userTableJson.getRound().setGames(jsonWithGamesAndTipps);

            return calcUserRanking(userTableJson, round.get(), round.get().getIndex());
        } else {
            return null;
        }
    }

    @Override
    public UserRankingTableDto calcUserRankingByRound(Long roundId) {
        Optional<GameListEntity> round = seasonManagerService.findRoundGames(roundId);

        UserRankingTableDto userTableJson = new UserRankingTableDto();
        userTableJson.setRound(DtoBuilder.toJson(round.get()));

        List<GameTippEntity> tipps = tippService.findTipps(roundId);

        // userTableJson.setRound(JsonBuilder.toJsonWithGames(round));
        List<GameDto> jsonWithGamesAndTipps = DtoBuilder.toJsonWithGamesAndTipps(round.get().unmodifiableList(),
                tipps);
        userTableJson.getRound().setGames(jsonWithGamesAndTipps);

        return calcUserRanking(userTableJson, round.get(), 0);
    }

    private UserRankingTableDto calcUserRanking(UserRankingTableDto userTableJson, GameListEntity round, int startIndex) {
        SeasonEntity season = seasonManagerService.findSeasonById(round.getSeason().getId());
        List<UserResult> calculatedRanking = communityCalculatorService.calculateRanking(
                CommunityService.defaultPlayerGroup(season.getReference()),
                SeasonRange.of(startIndex, round.getIndex()));

        for (UserResult ur : calculatedRanking) {
            UserRankingDto userJson = DtoBuilder.toJson(ur);
            userTableJson.addUser(userJson);
        }

        SeasonDto seasonJson = DtoBuilder.toJson(round.getSeason());
        userTableJson.setSeason(seasonJson);

        findNextAndPrevRound(round, userTableJson);

        return userTableJson;
    }

    private void findNextAndPrevRound(GameListEntity round, UserRankingTableDto userTableJson) {
        Optional<GameListEntity> nextNextRound = seasonManagerService.findNextRound(round.getId());
        userTableJson.getRound().setLastRound(!nextNextRound.isPresent());
        userTableJson.getRound().setTippable(isFinished(userTableJson.getRound()));
    }

    @Override
    public UserRankingTableDto calcUserRankingByNextRound(Long roundId) {
        Optional<GameListEntity> nextRound = seasonManagerService.findNextRound(roundId);
        if (nextRound.isPresent()) {
            return calcUserRankingByRound(nextRound.get().getId());
        }
        return null;
    }

    @Override
    public UserRankingTableDto calcUserRankingByPrevRound(Long roundId) {
        Optional<GameListEntity> prevRound = seasonManagerService.findPrevRound(roundId);
        if (prevRound.isPresent()) {
            return calcUserRankingByRound(prevRound.get().getId());
        }
        return null;
    }

    @Override
    public List<TeamDto> findAllTeams() {
        List<TeamEntity> teams = masterDataManagerService.findAllTeams();
        return DtoBuilder.toJsonWithTeams(teams);
    }

    @Override
    public List<TeamDto> findTeams(Optional<TeamType> teamType, String filter) {
        return TeamDtoMapper.map(masterDataManagerService.findTeams(teamType, filter));
    }

    @Override
    public List<SeasonDto> findAllSeason() {
        List<SeasonEntity> seasons = seasonManagerService.findAllSeasons();
        return DtoBuilder.toJsonWithSeasons(seasons);
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

    @Override
    public PingJson ping() {
        PingJson pingJson = new PingJson();
        pingJson.setDateTime(dateTimeProvider.currentDateTime());
        return pingJson;
    }

}
