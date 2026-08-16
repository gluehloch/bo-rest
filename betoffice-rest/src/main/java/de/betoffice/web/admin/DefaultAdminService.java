/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2000-2026 by Andre Winkler. All
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

package de.betoffice.web.admin;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.betoffice.openligadb.OpenligadbUpdateService;
import de.betoffice.service.AuthService;
import de.betoffice.service.CommunityService;
import de.betoffice.service.MasterDataManagerService;
import de.betoffice.service.SeasonManagerService;
import de.betoffice.storage.community.entity.CommunityReference;
import de.betoffice.storage.group.GroupTypeDto;
import de.betoffice.storage.group.entity.GroupTypeEntity;
import de.betoffice.storage.group.entity.GroupTeamDto;
import de.betoffice.storage.group.entity.GroupTypeDtoMapper;
import de.betoffice.storage.season.AddRoundJson;
import de.betoffice.storage.season.GameDto;
import de.betoffice.storage.season.RoundDto;
import de.betoffice.storage.season.SeasonDto;
import de.betoffice.storage.season.UpdateRoundDto;
import de.betoffice.storage.season.entity.GameEntity;
import de.betoffice.storage.season.entity.GameListEntity;
import de.betoffice.storage.season.entity.GroupEntity;
import de.betoffice.storage.season.entity.JsonBuilder;
import de.betoffice.storage.season.entity.SeasonEntity;
import de.betoffice.storage.season.entity.SeasonGroupTeamJson;
import de.betoffice.storage.season.entity.SeasonMemberJson;
import de.betoffice.storage.season.entity.SeasonMemberJsonMapper;
import de.betoffice.storage.season.entity.SeasonDtoMapper;
import de.betoffice.storage.session.entity.SessionEntity;
import de.betoffice.storage.team.TeamDto;
import de.betoffice.storage.team.TeamType;
import de.betoffice.storage.team.entity.TeamEntity;
import de.betoffice.storage.team.entity.TeamDtoMapper;
import de.betoffice.storage.time.DateTimeProvider;
import de.betoffice.storage.user.PartyDto;
import de.betoffice.storage.user.entity.Nickname;
import de.betoffice.storage.user.entity.PartyJsonMapper;
import de.betoffice.storage.user.entity.UserEntity;
import de.betoffice.validation.ValidationMessage;
import de.betoffice.validation.ValidationMessages;

/**
 * Betoffice administration JSON service interface.
 *
 * @author Andre Winkler
 */
@Component
@Transactional(readOnly = true)
public class DefaultAdminService implements AdminService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAdminService.class);

    private final DateTimeProvider dateTimeProvider;
    private final OpenligadbUpdateService openligadbUpdateService;
    private final MasterDataManagerService masterDataManagerService;
    private final SeasonManagerService seasonManagerService;
    private final CommunityService communityService;
    private final AuthService authService;
    private final RoundHandler roundHandler;

    public DefaultAdminService(
            DateTimeProvider dateTimeProvider,
            OpenligadbUpdateService openligadbUpdateService,
            MasterDataManagerService masterDataManagerService,
            SeasonManagerService seasonManagerService,
            CommunityService communityService,
            AuthService authService,
            RoundHandler roundHandler) {
        this.authService = authService;
        this.dateTimeProvider = dateTimeProvider;
        this.openligadbUpdateService = openligadbUpdateService;
        this.masterDataManagerService = masterDataManagerService;
        this.seasonManagerService = seasonManagerService;
        this.communityService = communityService;
        this.roundHandler = roundHandler;
    }

    // ------------------------------------------------------------------------

    @Override
    public void validateAdminSession(String token) {
        Optional<SessionEntity> session = authService.validateSession(token);

        if (!session.isPresent()) {
            throw new IllegalStateException("No valid session found for given token.");
        }

        if (session.get().getLogout() != null) {
            throw new IllegalStateException("User is already logged out for given token.");
        }

        ZonedDateTime loginDate = session.get().getLogin();
        if (loginDate.isBefore(ZonedDateTime.now(dateTimeProvider.defaultZoneId()).minusDays(3))) {
            throw new IllegalStateException("Login token is older than 3 days. ");
        }

        if (!session.get().getUser().isAdmin()) {
            throw new IllegalStateException("This is not an admin user token.");
        }
    }

    // ------------------------------------------------------------------------

    @Override
    @Transactional
    public RoundDto reconcileRoundWithOpenligadb(String token, Long seasonId, Long roundId) {
        SeasonEntity season = seasonManagerService.findSeasonById(seasonId);
        GameListEntity round = seasonManagerService.findRound(roundId);

        if (round == null) {
            openligadbUpdateService.createOrUpdateRound(seasonId, 0);
        } else {
            openligadbUpdateService.createOrUpdateRound(round.getSeason().getId(), round.getIndex());
        }

        GameListEntity updatedGameList = seasonManagerService.findNextRound(roundId)
                .orElseGet(() -> seasonManagerService.findFirstRound(season).orElseThrow());
        return JsonBuilder.toJsonWithGames(seasonManagerService.findRoundGames(updatedGameList.getId()).get());
    }

    @Override
    @Transactional
    public RoundDto mountRoundWithOpenligadb(String token, Long seasonId, Long roundId) {
        SeasonEntity season = seasonManagerService.findSeasonById(seasonId);
        GameListEntity round = seasonManagerService.findRound(roundId);

        if (round == null) {
            openligadbUpdateService.createOrUpdateRound(seasonId, 0);
        } else {
            openligadbUpdateService.createOrUpdateRound(seasonId, round.getIndex() + 1);
        }

        GameListEntity updatedGameList = seasonManagerService.findNextRound(roundId)
                .orElseGet(() -> seasonManagerService.findFirstRound(season).orElseThrow());
        return JsonBuilder.toJsonWithGames(seasonManagerService.findRoundGames(updatedGameList.getId()).get());
    }

    // -- team administration -------------------------------------------------

    @Override
    public List<TeamDto> findTeams(Optional<TeamType> teamType, String filter) {
        return TeamDtoMapper.map(masterDataManagerService.findTeams(teamType, filter));
    }

    @Override
    public TeamDto findTeam(long teamId) {
        TeamEntity team = masterDataManagerService.findTeamById(teamId);
        return TeamDtoMapper.map(team, new TeamDto());
    }

    @Override
    public List<TeamDto> findTeams() {
        return TeamDtoMapper.map(masterDataManagerService.findAllTeams());
    }

    @Override
    @Transactional
    public TeamDto addTeam(TeamDto teamJson) {
        TeamEntity team = TeamDtoMapper.reverse(teamJson, new TeamEntity());
        masterDataManagerService.createTeam(team);
        return TeamDtoMapper.map(team, teamJson);
    }

    @Override
    @Transactional
    public TeamDto updateTeam(TeamDto teamJson) {
        TeamEntity storedTeam = masterDataManagerService.findTeamById(teamJson.getId());
        TeamEntity team = TeamDtoMapper.reverse(teamJson, storedTeam);
        masterDataManagerService.updateTeam(team);
        return teamJson;
    }

    // -- user administration -------------------------------------------------

    public PartyDto findUser(long userId) {
        UserEntity user = communityService.findUser(userId);
        return PartyJsonMapper.map(user, new PartyDto());
    }

    @Override
    public List<PartyDto> findUsers() {
        return PartyJsonMapper.map(communityService.findAllUsers());
    }

    @Override
    @Transactional
    public PartyDto addUser(PartyDto partyJson) {
        UserEntity user = PartyJsonMapper.reverse(partyJson, new UserEntity());
        user = communityService.createUser(user);
        return PartyJsonMapper.map(user, partyJson);
    }

    @Override
    @Transactional
    public PartyDto updateUser(PartyDto partyJson) {
        communityService.updateUser(
                true,
                Nickname.of(partyJson.getNickname()),
                partyJson.getName(),
                partyJson.getSurname(),
                partyJson.getMail(),
                partyJson.isEmailNotificationEnabled(),
                partyJson.getPhone());
        return partyJson;
    }

    // -- season administration -----------------------------------------------

    @Override
    @Transactional
    public SeasonDto addSeason(SeasonDto seasonJson) {
        SeasonEntity season = SeasonDtoMapper.reverse(seasonJson, new SeasonEntity());
        masterDataManagerService.createSeason(season);
        return seasonJson;
    }

    @Override
    @Transactional
    public SeasonDto updateSeason(SeasonDto seasonJson) {
        SeasonEntity season = seasonManagerService.findSeasonById(seasonJson.getId());
        season = SeasonDtoMapper.reverse(seasonJson, season);
        masterDataManagerService.updateSeason(season);

        return SeasonDtoMapper.map(season, seasonJson);
    }

    @Override
    @Transactional
    public ValidationMessages updateRoundAndGames(long seasonId, long roundId, RoundDto round) {
        if (roundId != round.getId()) {
            LOG.error("Round id from path variable {} does not match round id from request body {}.", roundId,
                    round.getId());
            return ValidationMessages.of(
                    List.of(ValidationMessage.error(ValidationMessage.MessageType.ROUND_ID_MISMATCH, roundId,
                            round.getId())));
        }

        final Optional<GameListEntity> roundEntity = seasonManagerService.findRoundGames(round.getId());
        if (roundEntity.isEmpty()) {
            LOG.error("Can´t find round with id={}.", round.getId());
            return ValidationMessages.of(
                    List.of(ValidationMessage.error(ValidationMessage.MessageType.ROUND_ID_NOT_FOUND,
                            round.getId())));
        }

        final List<GameEntity> games = new ArrayList<>();
        for (GameDto match : round.getGames()) {
            GameEntity game = roundEntity.get().getById(match.getId());
            updateGame(match, game);
            games.add(game);
        }

        seasonManagerService.updateMatch(games);
        return ValidationMessages.ok();
    }

    @Override
    @Transactional
    public void updateGame(GameDto gameJson) {
        GameEntity game = seasonManagerService.findMatch(gameJson.getId());
        game.setDateTime(gameJson.getDateTime());
        updateGame(gameJson, game);
        seasonManagerService.updateMatch(game);
    }

    // TODO Gehoert sowas eher in einen JSON-Mapper? JsonAssembler | JsonBuilder?
    private void updateGame(GameDto match, GameEntity game) {
        game.setPlayed(match.isFinished());
        game.setKo(match.isKo());
        game.setResult(match.getResult().getHomeGoals(),
                match.getResult().getGuestGoals());
        game.setHalfTimeGoals(match.getHalfTimeResult().getHomeGoals(),
                match.getHalfTimeResult().getGuestGoals());
        game.setOverTimeGoals(match.getOvertimeResult().getHomeGoals(),
                match.getOvertimeResult().getGuestGoals());
        game.setPenaltyGoals(match.getPenaltyResult().getHomeGoals(),
                match.getPenaltyResult().getGuestGoals());
    }

    // -- user / season member administration ---------------------------------

    @Override
    public List<SeasonMemberJson> findPotentialSeasonMembers(long seasonId) {
        SeasonEntity season = seasonManagerService.findSeasonById(seasonId);
        CommunityReference defaultPlayerGroup = CommunityService.defaultPlayerGroup(season.getReference());
        Set<UserEntity> activatedUsers = communityService.findMembers(defaultPlayerGroup);
        List<UserEntity> users = communityService.findAllUsers();
        users.removeAll(activatedUsers);
        return SeasonMemberJsonMapper.map(users);
    }

    @Override
    public List<SeasonMemberJson> findAllSeasonMembers(long seasonId) {
        SeasonEntity season = seasonManagerService.findSeasonById(seasonId);
        CommunityReference defaultPlayerGroup = CommunityService.defaultPlayerGroup(season.getReference());
        Set<UserEntity> activatedUsers = communityService.findMembers(defaultPlayerGroup);
        return SeasonMemberJsonMapper.map(activatedUsers);
    }

    @Override
    @Transactional
    public List<SeasonMemberJson> addSeasonMembers(long seasonId, List<SeasonMemberJson> seasonMembers) {
        List<UserEntity> users = findUsers(seasonMembers);
        SeasonEntity season = seasonManagerService.findSeasonById(seasonId);
        CommunityReference defaultPlayerGroup = CommunityService.defaultPlayerGroup(season.getReference());

        Set<Nickname> nicknames = new HashSet<>(users.stream().map(UserEntity::getNickname).toList());
        communityService.addMembers(defaultPlayerGroup, nicknames);

        return findAllSeasonMembers(seasonId);
    }

    @Override
    @Transactional
    public List<SeasonMemberJson> removeSeasonMembers(long seasonId, List<SeasonMemberJson> seasonMembers) {
        SeasonEntity season = seasonManagerService.findSeasonById(seasonId);
        CommunityReference defaultPlayerGroup = CommunityService.defaultPlayerGroup(season.getReference());

        Set<Nickname> nicknames = new HashSet<>(
                seasonMembers.stream().map(sm -> Nickname.of(sm.getNickname())).toList());
        communityService.removeMembers(defaultPlayerGroup, nicknames);

        return findAllSeasonMembers(seasonId);
    }

    private List<UserEntity> findUsers(List<SeasonMemberJson> seasonMembers) {
        List<UserEntity> users = new ArrayList<>();
        for (SeasonMemberJson member : seasonMembers) {
            UserEntity user = communityService.findUser(member.getId());
            users.add(user);
        }
        return users;
    }

    @Override
    public List<GroupTypeDto> findGroupTypes() {
        return GroupTypeDtoMapper.map(masterDataManagerService.findAllGroupTypes());
    }

    @Override
    public GroupTypeDto findGroupType(long groupTypeId) {
        return GroupTypeDtoMapper.map(masterDataManagerService.findGroupType(groupTypeId), new GroupTypeDto());
    }

    @Override
    @Transactional
    public SeasonDto addGroupToSeason(SeasonDto seasonJson, GroupTypeDto groupTypeJson) {
        SeasonEntity season = seasonManagerService.findSeasonById(seasonJson.getId());
        GroupTypeEntity groupType = masterDataManagerService.findGroupType(groupTypeJson.getId());
        SeasonEntity season2 = seasonManagerService.addGroupType(season, groupType);
        return SeasonDtoMapper.map(season2, new SeasonDto());
    }

    @Override
    @Transactional
    public void removeGroupFromSeason(SeasonDto seasonJson, GroupTypeDto groupTypeJson) {
        SeasonEntity season = seasonManagerService.findSeasonById(seasonJson.getId());
        GroupTypeEntity groupType = masterDataManagerService.findGroupType(groupTypeJson.getId());
        seasonManagerService.removeGroupType(season, groupType);
    }

    @Override
    public SeasonGroupTeamJson findSeasonGroupsAndTeams(long seasonId) {
        SeasonEntity season = seasonManagerService.findSeasonById(seasonId);
        List<GroupEntity> groups = seasonManagerService.findGroups(season);
        SeasonGroupTeamJson seasonGroupTeamJson = new SeasonGroupTeamJson();

        for (GroupEntity group : groups) {
            List<TeamEntity> teams = seasonManagerService.findTeams(group);
            GroupTeamDto groupTeamJson = new GroupTeamDto();
            groupTeamJson.setGroupType(GroupTypeDtoMapper.map(group.getGroupType(), new GroupTypeDto()));
            groupTeamJson.setTeams(TeamDtoMapper.map(teams));
            seasonGroupTeamJson.getGroupTeams().add(groupTeamJson);
        }

        return seasonGroupTeamJson;
    }

    @Override
    public List<TeamDto> findSeasonGroupAndTeamCandidates(SeasonDto seasonJson, GroupTypeDto groupTypeJson) {
        SeasonEntity season = seasonManagerService.findSeasonById(seasonJson.getId());
        GroupTypeEntity groupType = masterDataManagerService.findGroupType(groupTypeJson.getId());
        List<TeamEntity> teams = seasonManagerService.findTeams(season, groupType);
        List<TeamEntity> teamCandidates = masterDataManagerService.findTeams(season.getTeamType());
        teamCandidates.removeAll(teams);

        return TeamDtoMapper.map(teamCandidates);
    }

    @Override
    @Transactional
    public void addTeamToGroup(SeasonDto seasonJson, GroupTypeDto groupTypeJson, TeamDto teamJson) {
        SeasonEntity season = seasonManagerService.findSeasonById(seasonJson.getId());
        GroupTypeEntity groupType = masterDataManagerService.findGroupType(groupTypeJson.getId());
        TeamEntity team = masterDataManagerService.findTeamById(teamJson.getId());
        seasonManagerService.addTeam(season, groupType, team);
    }

    @Override
    @Transactional
    public void removeTeamFromGroup(SeasonDto seasonJson, GroupTypeDto groupTypeJson, TeamDto teamJson) {
        SeasonEntity season = seasonManagerService.findSeasonById(seasonJson.getId());
        GroupTypeEntity groupType = masterDataManagerService.findGroupType(groupTypeJson.getId());
        TeamEntity team = masterDataManagerService.findTeamById(teamJson.getId());
        seasonManagerService.removeTeam(season, groupType, team);
    }

    @Override
    @Transactional
    public ValidationMessages addRound(long seasonId, AddRoundJson round) {
        return roundHandler.addRound(seasonId, round);
    }

    @Override
    @Transactional
    public ValidationMessages updateRound(long seasonId, long roundId, UpdateRoundDto round) {
        return roundHandler.updateRound(seasonId, roundId, round);
    }

}
