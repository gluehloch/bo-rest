/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2000-2020 by Andre Winkler. All
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
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.betoffice.openligadb.OpenligadbUpdateService;
import de.betoffice.web.json.GameJson;
import de.betoffice.web.json.PartyJson;
import de.betoffice.web.json.RoundJson;
import de.betoffice.web.json.SeasonJson;
import de.betoffice.web.json.SeasonMemberJson;
import de.betoffice.web.json.TeamJson;
import de.betoffice.web.json.builder.PartyJsonMapper;
import de.betoffice.web.json.builder.SeasonJsonMapper;
import de.betoffice.web.json.builder.SeasonMemberJsonMapper;
import de.betoffice.web.json.builder.TeamJsonMapper;
import de.winkler.betoffice.service.AuthService;
import de.winkler.betoffice.service.DateTimeProvider;
import de.winkler.betoffice.service.MasterDataManagerService;
import de.winkler.betoffice.service.SeasonManagerService;
import de.winkler.betoffice.storage.Game;
import de.winkler.betoffice.storage.GameList;
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
        User user = masterDataManagerService.findUser(userId);
        return new PartyJsonMapper().map(user, new PartyJson());
    }

    @Override
    public List<PartyJson> findUsers() {
        return new PartyJsonMapper()
                .map(masterDataManagerService.findAllUsers());
    }

    @Override
    public PartyJson addUser(PartyJson partyJson) {
        PartyJsonMapper mapper = new PartyJsonMapper();
        User user = mapper.reverse(partyJson, new User());
        user = masterDataManagerService.createUser(user);
        return mapper.map(user, partyJson);
    }

    @Override
    public PartyJson updateUser(PartyJson partyJson) {
        User storedUser = masterDataManagerService.findUser(partyJson.getId());
        User user = new PartyJsonMapper().reverse(partyJson, storedUser);
        masterDataManagerService.updateUser(user);
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
    private void updateGame(GameJson match, Game game) {
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
        List<User> activatedUsers = seasonManagerService
                .findActivatedUsers(season);
        List<User> users = masterDataManagerService.findAllUsers();

        users.removeAll(activatedUsers);

        SeasonMemberJsonMapper mapper = new SeasonMemberJsonMapper();
        List<SeasonMemberJson> seasonMembers = mapper.map(users);

        return seasonMembers;
    }

    @Override
    public List<SeasonMemberJson> findAllSeasonMembers(long seasonId) {
        Season season = seasonManagerService.findSeasonById(seasonId);
        List<User> activatedUsers = seasonManagerService.findActivatedUsers(season);

        SeasonMemberJsonMapper mapper = new SeasonMemberJsonMapper();
        List<SeasonMemberJson> seasonMembers = mapper.map(activatedUsers);

        return seasonMembers;
    }

    @Override
    public List<SeasonMemberJson> addSeasonMembers(long seasonId, List<SeasonMemberJson> seasonMembers) {
        List<User> users = findUsers(seasonMembers);
        Season season = seasonManagerService.findSeasonById(seasonId);

        seasonManagerService.addUsers(season, users);

        return findAllSeasonMembers(seasonId);
    }

    @Override
    public List<SeasonMemberJson> removeSeasonMembers(long seasonId, List<SeasonMemberJson> seasonMembers) {
        List<User> users = findUsers(seasonMembers);
        Season season = seasonManagerService.findSeasonById(seasonId);

        seasonManagerService.removeUsers(season, users);

        return findAllSeasonMembers(seasonId);
    }

    private List<User> findUsers(List<SeasonMemberJson> seasonMembers) {
        List<User> users = new ArrayList<>();
        for (SeasonMemberJson member : seasonMembers) {
            User user = masterDataManagerService.findUser(member.getId());
            users.add(user);
        }
        return users;
    }

}
