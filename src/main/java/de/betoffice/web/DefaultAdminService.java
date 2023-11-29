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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.betoffice.openligadb.OpenligadbUpdateService;
import de.betoffice.web.json.GameJson;
import de.betoffice.web.json.GroupTeamJson;
import de.betoffice.web.json.GroupTypeJson;
import de.betoffice.web.json.IGameJson;
import de.betoffice.web.json.PartyJson;
import de.betoffice.web.json.RoundJson;
import de.betoffice.web.json.SeasonGroupTeamJson;
import de.betoffice.web.json.SeasonJson;
import de.betoffice.web.json.SeasonMemberJson;
import de.betoffice.web.json.TeamJson;
import de.betoffice.web.json.builder.GroupTypeJsonMapper;
import de.betoffice.web.json.builder.PartyJsonMapper;
import de.betoffice.web.json.builder.SeasonJsonMapper;
import de.betoffice.web.json.builder.SeasonMemberJsonMapper;
import de.betoffice.web.json.builder.TeamJsonMapper;
import de.winkler.betoffice.service.AuthService;
import de.winkler.betoffice.service.CommunityService;
import de.winkler.betoffice.service.DateTimeProvider;
import de.winkler.betoffice.service.MasterDataManagerService;
import de.winkler.betoffice.service.SeasonManagerService;
import de.winkler.betoffice.storage.CommunityReference;
import de.winkler.betoffice.storage.Game;
import de.winkler.betoffice.storage.GameList;
import de.winkler.betoffice.storage.Group;
import de.winkler.betoffice.storage.GroupType;
import de.winkler.betoffice.storage.Nickname;
import de.winkler.betoffice.storage.Season;
import de.winkler.betoffice.storage.Session;
import de.winkler.betoffice.storage.Team;
import de.winkler.betoffice.storage.User;

/**
 * Betoffice administration JSON service interface.
 *
 * @author Andre Winkler
 */
@Component("betofficeAdminService")
public class DefaultAdminService implements AdminService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAdminService.class);

    @Autowired
    private DateTimeProvider dateTimeProvider;

    @Autowired
    private OpenligadbUpdateService openligadbUpdateService;

    @Autowired
    private MasterDataManagerService masterDataManagerService;

    @Autowired
    private SeasonManagerService seasonManagerService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private AuthService authService;

    // ------------------------------------------------------------------------

    @Override
    public void validateAdminSession(String token) {
        Optional<Session> session = authService.validateSession(token);

        if (!session.isPresent()) {
            throw new AccessDeniedException();
            // TODO Is this better? throw new WebApplicationException(Status.FORBIDDEN);
        }

        if (session.get().getLogout() != null) {
            throw new AccessDeniedException();
        }

        ZonedDateTime loginDate = session.get().getLogin();
        if (loginDate.isBefore(ZonedDateTime.now(dateTimeProvider.defaultZoneId()).minusDays(3))) {
            throw new AccessDeniedException();
        }

        if (!session.get().getUser().isAdmin()) {
            throw new AccessDeniedException();
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public RoundJson reconcileRoundWithOpenligadb(String token, Long roundId) {
        GameList gameList = seasonManagerService.findRound(roundId);
        openligadbUpdateService.createOrUpdateRound(gameList.getSeason().getId(), gameList.getIndex());
        GameList updatedGameList = seasonManagerService.findRoundGames(roundId).orElseThrow();
        return JsonBuilder.toJsonWithGames(updatedGameList);
    }

    @Override
    public RoundJson mountRoundWithOpenligadb(String token, Long roundId) {
        GameList round = seasonManagerService.findRound(roundId);

        // TODO Dieser ganze Mechanismus funktioniert nur mit einer Liga.
        // WM und EM sind damit nicht abbildbar.
        // Welcher Spieltag soll gemountet werden? Der nächste Spieltag nach
        // dem letzten bekannten Spieltag vielleicht?

        // TODO Hack Bundesliga 2023/24
        if (round == null) {
            openligadbUpdateService.createOrUpdateRound(35L, 0);
        }
        
        if (round != null) {
            openligadbUpdateService.createOrUpdateRound(round.getSeason().getId(), round.getIndex() + 1);
        }

        // TODO ... get() .... get() .... get()
        Optional<GameList> updatedGameList = seasonManagerService.findNextRound(roundId);
        return JsonBuilder.toJsonWithGames(seasonManagerService.findRoundGames(updatedGameList.get().getId()).get());
    }

    // -- team administration -------------------------------------------------

    @Override
    public TeamJson findTeam(long teamId) {
        Team team = masterDataManagerService.findTeamById(teamId);
        return new TeamJsonMapper().map(team, new TeamJson());
    }

    @Override
    public List<TeamJson> findTeams() {
        return new TeamJsonMapper()
                .map(masterDataManagerService.findAllTeams());
    }

    @Override
    public TeamJson addTeam(TeamJson teamJson) {
        TeamJsonMapper mapper = new TeamJsonMapper();
        Team team = new TeamJsonMapper().reverse(teamJson, new Team());
        masterDataManagerService.createTeam(team);
        return mapper.map(team, teamJson);
    }

    @Override
    public TeamJson updateTeam(TeamJson teamJson) {
        Team storedTeam = masterDataManagerService
                .findTeamById(teamJson.getId());
        Team team = new TeamJsonMapper().reverse(teamJson, storedTeam);
        masterDataManagerService.updateTeam(team);
        return teamJson;
    }

    // -- user administration -------------------------------------------------

    public PartyJson findUser(long userId) {
        User user = communityService.findUser(userId);
        return new PartyJsonMapper().map(user, new PartyJson());
    }

    @Override
    public List<PartyJson> findUsers() {
        return new PartyJsonMapper().map(communityService.findAllUsers());
    }

    @Override
    public PartyJson addUser(PartyJson partyJson) {
        PartyJsonMapper mapper = new PartyJsonMapper();
        User user = mapper.reverse(partyJson, new User());
        user = communityService.createUser(user);
        return mapper.map(user, partyJson);
    }

    @Override
    public PartyJson updateUser(PartyJson partyJson) {
        User storedUser = communityService.findUser(partyJson.getId());
        User user = new PartyJsonMapper().reverse(partyJson, storedUser);
        communityService.updateUser(user);
        return partyJson;
    }

    // -- season administration -----------------------------------------------

    @Override
    public SeasonJson addSeason(SeasonJson seasonJson) {
        Season season = new SeasonJsonMapper().reverse(seasonJson,
                new Season());
        masterDataManagerService.createSeason(season);
        return seasonJson;
    }

    @Override
    public SeasonJson updateSeason(SeasonJson seasonJson) {
        Season season = seasonManagerService.findSeasonById(seasonJson.getId());
        season = new SeasonJsonMapper().reverse(seasonJson, season);
        masterDataManagerService.updateSeason(season);

        return new SeasonJsonMapper().map(season, seasonJson);
    }

    @Override
    public void updateRound(RoundJson roundJson) {
        Optional<GameList> round = seasonManagerService.findRoundGames(roundJson.getId());
        if (round.isEmpty()) {
            LOG.error("Can´t find round with id={}.", roundJson.getId());
            return;
        } else {
            List<Game> games = new ArrayList<>();
            for (GameJson match : roundJson.getGames()) {
                Game game = round.get().getById(match.getId());
                updateGame(match, game);
                games.add(game);
            }

            seasonManagerService.updateMatch(games);
        }
    }

    @Override
    public void updateGame(GameJson gameJson) {
        Game game = seasonManagerService.findMatch(gameJson.getId());
        game.setDateTime(gameJson.getDateTime());
        updateGame(gameJson, game);
        seasonManagerService.updateMatch(game);
    }

    // TODO Gehoert sowas eher in einen JSON-Mapper? JsonAssembler | JsonBuilder?
    private void updateGame(IGameJson match, Game game) {
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
        Season season = seasonManagerService.findSeasonById(seasonId);
        CommunityReference defaultPlayerGroup = CommunityService.defaultPlayerGroup(season.getReference());
        Set<User> activatedUsers = communityService.findMembers(defaultPlayerGroup);
        List<User> users = communityService.findAllUsers();

        users.removeAll(activatedUsers);

        SeasonMemberJsonMapper mapper = new SeasonMemberJsonMapper();
        return mapper.map(users);
    }

    @Override
    public List<SeasonMemberJson> findAllSeasonMembers(long seasonId) {
        Season season = seasonManagerService.findSeasonById(seasonId);
        CommunityReference defaultPlayerGroup = CommunityService.defaultPlayerGroup(season.getReference());
        Set<User> activatedUsers = communityService.findMembers(defaultPlayerGroup);

        SeasonMemberJsonMapper mapper = new SeasonMemberJsonMapper();
        return mapper.map(activatedUsers);
    }

    @Override
    public List<SeasonMemberJson> addSeasonMembers(long seasonId, List<SeasonMemberJson> seasonMembers) {
        List<User> users = findUsers(seasonMembers);
        Season season = seasonManagerService.findSeasonById(seasonId);
        CommunityReference defaultPlayerGroup = CommunityService.defaultPlayerGroup(season.getReference());

        Set<Nickname> nicknames = new HashSet<>(users.stream().map(User::getNickname).toList());
        communityService.addMembers(defaultPlayerGroup, nicknames);

        return findAllSeasonMembers(seasonId);
    }

    @Override
    public List<SeasonMemberJson> removeSeasonMembers(long seasonId, List<SeasonMemberJson> seasonMembers) {
        Season season = seasonManagerService.findSeasonById(seasonId);
        CommunityReference defaultPlayerGroup = CommunityService.defaultPlayerGroup(season.getReference());

        Set<Nickname> nicknames = new HashSet<>(seasonMembers.stream().map(sm -> Nickname.of(sm.getNickname())).toList());
        communityService.removeMembers(defaultPlayerGroup, nicknames);

        return findAllSeasonMembers(seasonId);
    }

    private List<User> findUsers(List<SeasonMemberJson> seasonMembers) {
        List<User> users = new ArrayList<>();
        for (SeasonMemberJson member : seasonMembers) {
            User user = communityService.findUser(member.getId());
            users.add(user);
        }
        return users;
    }

	@Override
	public List<GroupTypeJson> findGroupTypes() {
		return new GroupTypeJsonMapper().map(masterDataManagerService.findAllGroupTypes());
	}

	@Override
	public GroupTypeJson findGroupType(long groupTypeId) {
		return new GroupTypeJsonMapper().map(masterDataManagerService.findGroupType(groupTypeId), new GroupTypeJson());
	}

	@Override
	public SeasonJson addGroupToSeason(SeasonJson seasonJson, GroupTypeJson groupTypeJson) {
		Season season = seasonManagerService.findSeasonById(seasonJson.getId());
		GroupType groupType = masterDataManagerService.findGroupType(groupTypeJson.getId());
		Season season2 = seasonManagerService.addGroupType(season, groupType);
		return new SeasonJsonMapper().map(season2, new SeasonJson());
	}

	@Override
	public void removeGroupFromSeason(SeasonJson seasonJson, GroupTypeJson groupTypeJson) {
		Season season = seasonManagerService.findSeasonById(seasonJson.getId());
		GroupType groupType = masterDataManagerService.findGroupType(groupTypeJson.getId());		
		seasonManagerService.removeGroupType(season, groupType);
	}
	
	@Override
	public SeasonGroupTeamJson findSeasonGroupsAndTeams(long seasonId) {
		Season season = seasonManagerService.findSeasonById(seasonId);
		List<Group> groups = seasonManagerService.findGroups(season);
		SeasonGroupTeamJson seasonGroupTeamJson = new SeasonGroupTeamJson();

		GroupTypeJsonMapper groupTypeMapper = new GroupTypeJsonMapper();
		TeamJsonMapper teamMapper = new TeamJsonMapper();
		
		for (Group group : groups) {
			List<Team> teams = seasonManagerService.findTeams(group);
			GroupTeamJson groupTeamJson = new GroupTeamJson();
			groupTeamJson.setGroupType(groupTypeMapper.map(group.getGroupType(), new GroupTypeJson()));
			groupTeamJson.setTeams(teamMapper.map(teams));
			seasonGroupTeamJson.getGroupTeams().add(groupTeamJson);
		}
		
		return seasonGroupTeamJson;
	}

	@Override
	public List<TeamJson> findSeasonGroupAndTeamCandidates(SeasonJson seasonJson, GroupTypeJson groupTypeJson) {
		Season season = seasonManagerService.findSeasonById(seasonJson.getId());
		GroupType groupType = masterDataManagerService.findGroupType(groupTypeJson.getId());
		List<Team> teams = seasonManagerService.findTeams(season, groupType);
		List<Team> teamCandidates = masterDataManagerService.findTeams(season.getTeamType());
		teamCandidates.removeAll(teams);
		
		return new TeamJsonMapper().map(teamCandidates);
	}

	@Override
    public void addTeamToGroup(SeasonJson seasonJson, GroupTypeJson groupTypeJson, TeamJson teamJson) {
		Season season = seasonManagerService.findSeasonById(seasonJson.getId());
		GroupType groupType = masterDataManagerService.findGroupType(groupTypeJson.getId());
		Team team = masterDataManagerService.findTeamById(teamJson.getId());
		seasonManagerService.addTeam(season, groupType, team);
	}

    @Override
    public void removeTeamFromGroup(SeasonJson seasonJson, GroupTypeJson groupTypeJson, TeamJson teamJson) {
        Season season = seasonManagerService.findSeasonById(seasonJson.getId());
        GroupType groupType = masterDataManagerService.findGroupType(groupTypeJson.getId());
        Team team = masterDataManagerService.findTeamById(teamJson.getId());
        seasonManagerService.removeTeam(season, groupType, team);
    }

}
