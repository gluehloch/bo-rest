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

import de.betoffice.storage.group.GroupTypeDto;
import de.betoffice.storage.season.AddRoundJson;
import de.betoffice.storage.season.GameDto;
import de.betoffice.storage.season.RoundDto;
import de.betoffice.storage.season.SeasonDto;
import de.betoffice.storage.season.SeasonGroupTeamDto;
import de.betoffice.storage.season.UpdateRoundDto;
import de.betoffice.storage.season.entity.SeasonMemberDto;
import de.betoffice.storage.team.TeamDto;
import de.betoffice.storage.team.TeamType;
import de.betoffice.storage.user.PartyDto;
import de.betoffice.validation.ValidationMessages;

/**
 * Betoffice administration JSON service interface
 *
 * @author Andre Winkler
 */
public interface AdminService {

    /**
     * Validate admin session.
     *
     * @param  token             the session token
     * @throws SecurityException if the token is invalid, expired, or does not represent a valid administrator session
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
    RoundDto reconcileRoundWithOpenligadb(String token, Long seasonId, Long roundId);

    /**
     * Append round and game informations
     * 
     * @param  token    the session id / security token
     * @param  seasonId the season id
     * @param  roundId  create or update the round after roundId
     * @return          The mounted round and games.
     */
    RoundDto mountRoundWithOpenligadb(String token, Long seasonId, Long roundId);

    // -- team administration -------------------------------------------------

    /**
     * Find a team
     * 
     * @param  teamId
     * @return        the team
     */
    TeamDto findTeam(long teamId);

    /**
     * Find all teams
     * 
     * @return all teams
     */
    List<TeamDto> findTeams();

    /**
     * Find all teams
     * 
     * @param  teamType   the requested team type
     * @param  nameFilter a filter for the team name
     * @return            all teams matching the request
     */
    List<TeamDto> findTeams(Optional<TeamType> teamType, String nameFilter);

    /**
     * Add a new team.
     * 
     * @param  teamJson the new team
     * @return          the team
     */
    TeamDto addTeam(TeamDto teamJson);

    /**
     * Upadate a new team.
     * 
     * @param  teamJson the team to update
     * @return          the team
     */
    TeamDto updateTeam(TeamDto teamJson);

    // -- user administration -------------------------------------------------

    /**
     * Find a user.
     * 
     * @param  userId the user id
     * @return        the user
     */
    PartyDto findUser(long userId);

    /**
     * Returns all known users.
     * 
     * @return a list of all known users.
     */
    List<PartyDto> findUsers();

    /**
     * Create a new party.
     * 
     * @param  user the new user/party
     * @return      the created party
     */
    PartyDto addUser(PartyDto user);

    /**
     * Update a party
     * 
     * @param  user the updated user/party
     * @return      the updated party
     */
    PartyDto updateUser(PartyDto user);

    // -- group administration -----------------------------------------------

    List<GroupTypeDto> findGroupTypes();

    GroupTypeDto findGroupType(long groupTypeId);

    SeasonDto addGroupToSeason(SeasonDto season, GroupTypeDto groupType);

    void removeGroupFromSeason(SeasonDto seasonJson, GroupTypeDto groupTypeJson);

    SeasonGroupTeamDto findSeasonGroupsAndTeams(long seasonId);

    List<TeamDto> findSeasonGroupAndTeamCandidates(SeasonDto seasonJson, GroupTypeDto groupTypeJson);

    void addTeamToGroup(SeasonDto seasonJson, GroupTypeDto groupTypeJson, TeamDto team);

    void removeTeamFromGroup(SeasonDto seasonJson, GroupTypeDto groupTypeJson, TeamDto teamJson);

    // -- season administration -----------------------------------------------

    /**
     * Create a new season.
     * 
     * @param  season the new season
     * @return        a new season
     */
    SeasonDto addSeason(SeasonDto season);

    /**
     * Update a season
     * 
     * @param  season the season to update
     * @return        the updated season
     */
    SeasonDto updateSeason(SeasonDto season);

    /**
     * Update a round and its games with the data from the given round.
     * 
     * @param  round the round to update
     * @return       operation feedback
     */
    ValidationMessages updateRoundAndGames(long seasonId, long roundId, RoundDto round);

    /**
     * Erstellt einen neuen Spieltag.
     * 
     * @param seasonId die ID der Meisterschaft aka Saison, zu der der Spieltag hinzugefügt werden soll
     * @param round    die Daten des neuen Spieltags
     */
    ValidationMessages addRound(long seasonId, AddRoundJson round);

    /**
     * Aktualisiert eine Runde mit den Daten aus dem übergebenen UpdateRoundJson Objekt. Es werden nur die Daten
     * aktualisiert, die im UpdateRoundJson Objekt gesetzt sind. Alle anderen Daten der Runde bleiben unverändert.
     * 
     * @param  round
     * @return       operation feedback
     */
    ValidationMessages updateRound(long seasonId, long roundId, UpdateRoundDto round);

    /**
     * Update a game
     * 
     * @param game the game to update
     */
    void updateGame(GameDto game);

    // -- season member administration ----------------------------------------

    /**
     * Find all potential season members. So all users who are not member of the requested season.
     * 
     * @param  seasonId the season id
     * @return          a list of potential season members
     */
    List<SeasonMemberDto> findPotentialSeasonMembers(long seasonId);

    /**
     * Find all season members.
     * 
     * @param  seasonId the season id
     * @return          a list of season members
     */
    List<SeasonMemberDto> findAllSeasonMembers(long seasonId);

    /**
     * Add some members to a season.
     * 
     * @param  seasonId      the season id
     * @param  seasonMembers the new season members
     * @return               a list of current season members
     */
    List<SeasonMemberDto> addSeasonMembers(long seasonId,
            List<SeasonMemberDto> seasonMembers);

    /**
     * Remove some members from a season.
     * 
     * @param  seasonId      the season id
     * @param  seasonMembers the new season members
     * @return               a list of current season members
     */
    List<SeasonMemberDto> removeSeasonMembers(long seasonId,
            List<SeasonMemberDto> seasonMembers);

}
