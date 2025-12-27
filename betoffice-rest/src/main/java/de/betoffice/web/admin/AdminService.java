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

package de.betoffice.web.admin;

import java.util.List;
import java.util.Optional;

import de.betoffice.storage.team.TeamType;
import de.betoffice.web.json.GameJson;
import de.betoffice.web.json.GroupTypeJson;
import de.betoffice.web.json.PartyJson;
import de.betoffice.web.json.RoundJson;
import de.betoffice.web.json.SeasonGroupTeamJson;
import de.betoffice.web.json.SeasonJson;
import de.betoffice.web.json.SeasonMemberJson;
import de.betoffice.web.json.TeamJson;

/**
 * Betoffice administration JSON service interface
 *
 * @author Andre Winkler
 */
public interface AdminService {

    /**
     * Validate admin session.
     *
     * @param token the session token
     */
    void validateAdminSession(String token);

    // -- openligadb services -------------------------------------------------

    /**
     * Update round and game informations with the data from openligadb. (reconcile = abgleichen)
     * 
     * @param  token    the session id / security token
     * @param  seasonId the season id
     * @param  roundId  The round to update
     * @return          The updated round and games
     */
    RoundJson reconcileRoundWithOpenligadb(String token, Long seasonId, Long roundId);

    /**
     * Append round and game informations
     * 
     * @param  token    the session id / security token
     * @param  seasonId the season id
     * @param  roundId  create or update the round after roundId
     * @return          The mounted round and games.
     */
    RoundJson mountRoundWithOpenligadb(String token, Long seasonId, Long roundId);

    // -- team administration -------------------------------------------------

    /**
     * Find a team
     * 
     * @param  teamId
     * @return        the team
     */
    TeamJson findTeam(long teamId);

    /**
     * Find all teams
     * 
     * @return all teams
     */
    List<TeamJson> findTeams();

    /**
     * Find all teams
     * 
     * @param  teamType   the requested team type
     * @param  nameFilter a filter for the team name
     * @return            all teams matching the request
     */
    List<TeamJson> findTeams(Optional<TeamType> teamType, String nameFilter);

    /**
     * Add a new team.
     * 
     * @param  teamJson the new team
     * @return          the team
     */
    TeamJson addTeam(TeamJson teamJson);

    /**
     * Upadate a new team.
     * 
     * @param  teamJson the team to update
     * @return          the team
     */
    TeamJson updateTeam(TeamJson teamJson);

    // -- user administration -------------------------------------------------

    /**
     * Find a user.
     * 
     * @param  userId the user id
     * @return        the user
     */
    PartyJson findUser(long userId);

    /**
     * Returns all known users.
     * 
     * @return a list of all known users.
     */
    List<PartyJson> findUsers();

    /**
     * Create a new party.
     * 
     * @param  user the new user/party
     * @return      the created party
     */
    PartyJson addUser(PartyJson user);

    /**
     * Update a party
     * 
     * @param  user the updated user/party
     * @return      the updated party
     */
    PartyJson updateUser(PartyJson user);

    // -- group administration -----------------------------------------------

    List<GroupTypeJson> findGroupTypes();

    GroupTypeJson findGroupType(long groupTypeId);

    SeasonJson addGroupToSeason(SeasonJson season, GroupTypeJson groupType);

    void removeGroupFromSeason(SeasonJson seasonJson, GroupTypeJson groupTypeJson);

    SeasonGroupTeamJson findSeasonGroupsAndTeams(long seasonId);

    List<TeamJson> findSeasonGroupAndTeamCandidates(SeasonJson seasonJson, GroupTypeJson groupTypeJson);

    void addTeamToGroup(SeasonJson seasonJson, GroupTypeJson groupTypeJson, TeamJson team);

    void removeTeamFromGroup(SeasonJson seasonJson, GroupTypeJson groupTypeJson, TeamJson teamJson);

    // -- season administration -----------------------------------------------

    /**
     * Create a new season.
     * 
     * @param  season the new season
     * @return        a new season
     */
    SeasonJson addSeason(SeasonJson season);

    /**
     * Update a season
     * 
     * @param  season the season to update
     * @return        the updated season
     */
    SeasonJson updateSeason(SeasonJson season);

    /**
     * Update a round
     * 
     * @param round the round to update
     */
    void updateRound(RoundJson round);

    /**
     * Update a game
     * 
     * @param game the game to update
     */
    void updateGame(GameJson game);

    // -- season member administration ----------------------------------------

    /**
     * Find all potential season members. So all users who are not member of the requested season.
     * 
     * @param  seasonId the season id
     * @return          a list of potential season members
     */
    List<SeasonMemberJson> findPotentialSeasonMembers(long seasonId);

    /**
     * Find all season members.
     * 
     * @param  seasonId the season id
     * @return          a list of season members
     */
    List<SeasonMemberJson> findAllSeasonMembers(long seasonId);

    /**
     * Add some members to a season.
     * 
     * @param  seasonId      the season id
     * @param  seasonMembers the new season members
     * @return               a list of current season members
     */
    List<SeasonMemberJson> addSeasonMembers(long seasonId,
            List<SeasonMemberJson> seasonMembers);

    /**
     * Remove some members from a season.
     * 
     * @param  seasonId      the season id
     * @param  seasonMembers the new season members
     * @return               a list of current season members
     */
    List<SeasonMemberJson> removeSeasonMembers(long seasonId,
            List<SeasonMemberJson> seasonMembers);

}
