/*
 * ============================================================================
 * Project betoffice-jweb Copyright (c) 2015-2024 by Andre Winkler. All rights
 * reserved.
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.betoffice.web.AccessDeniedException;
import de.betoffice.web.BetofficeHttpConsts;
import de.betoffice.web.json.GameJson;
import de.betoffice.web.json.GroupTypeJson;
import de.betoffice.web.json.IGameJson;
import de.betoffice.web.json.PartyJson;
import de.betoffice.web.json.RoundAndTableJson;
import de.betoffice.web.json.RoundJson;
import de.betoffice.web.json.SeasonGroupTeamJson;
import de.betoffice.web.json.SeasonJson;
import de.betoffice.web.json.SeasonMemberJson;
import de.betoffice.web.json.TeamJson;
import de.betoffice.web.season.BetofficeService;
import de.winkler.betoffice.storage.enums.TeamType;

/**
 * The administration part of the betoffice.
 * 
 * @author Andre Winkler
 */
@CrossOrigin
@RestController
@RequestMapping("/chiefoperator")
public class AdministrationController {

    // ------------------------------------------------------------------------
    // The beans
    // ------------------------------------------------------------------------

    // -- betofficeBasicJsonService -------------------------------------------

    private BetofficeService betofficeBasicJsonService;

    @Autowired
    public void setBetofficeBasicJsonService(BetofficeService _betofficeBasicJsonService) {

        betofficeBasicJsonService = _betofficeBasicJsonService;
    }

    // -- betofficeAdminJsonService -------------------------------------------

    private AdminService betofficeAdminJsonService;

    @Autowired
    public void setBetofficeAdminJsonService(AdminService _betofficeAdminJsonService) {

        betofficeAdminJsonService = _betofficeAdminJsonService;
    }

    // -- openligadb ----------------------------------------------------------

    @RequestMapping(value = "/season/{seasonId}/round/{roundId}/group/{groupId}/ligadbupdate", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public RoundAndTableJson updateRoundByOpenligaDb(
            @PathVariable("seasonId") Long seasonId,
            @PathVariable("roundId") Long roundId,
            @PathVariable("groupId") Long groupId,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminJsonService.validateAdminSession(token);
        betofficeAdminJsonService.reconcileRoundWithOpenligadb(token, seasonId, roundId);
        return betofficeBasicJsonService.findRoundTable(seasonId, roundId, groupId);
    }

    @RequestMapping(value = "/season/{seasonId}/round/{roundId}/group/{groupId}/ligadbcreate", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public RoundAndTableJson createOrUpdateRoundByOpenligaDb(
            @PathVariable("seasonId") Long seasonId,
            @PathVariable("roundId") Long roundId,
            @PathVariable("groupId") Long groupId,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminJsonService.validateAdminSession(token);
        betofficeAdminJsonService.mountRoundWithOpenligadb(token, seasonId, roundId);
        return betofficeBasicJsonService.findRoundTable(seasonId, roundId, groupId);
    }

    // -- round update --------------------------------------------------------

    @RequestMapping(value = "/season/{seasonId}/round/{roundId}/group/{groupId}/update", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public RoundAndTableJson updateRound(
            @PathVariable("seasonId") Long seasonId,
            @PathVariable("roundId") Long roundId,
            @PathVariable("groupId") Long groupId,
            @RequestBody RoundJson roundJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminJsonService.validateAdminSession(token);
        betofficeAdminJsonService.updateRound(roundJson);
        return betofficeBasicJsonService.findRoundTable(seasonId, roundId, groupId);
    }

    @RequestMapping(value = "/game/update", method = RequestMethod.POST, headers = { "Content-type=application/json" })
    public IGameJson updateGame(@RequestBody GameJson gameJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminJsonService.validateAdminSession(token);
        betofficeAdminJsonService.updateGame(gameJson);
        return betofficeBasicJsonService.findGame(gameJson.getId());
    }

    // -- season administration -----------------------------------------------

    @CrossOrigin
    @RequestMapping(value = "/season/{seasonId}", method = RequestMethod.GET, headers = {
            "Content-type=application/json" })
    public SeasonJson findSeason(@PathVariable("seasonId") Long seasonId) {
        return betofficeBasicJsonService.findSeasonById(seasonId);
    }

    @CrossOrigin
    @RequestMapping(value = "/season", method = RequestMethod.GET, headers = { "Content-type=application/json" })
    public List<SeasonJson> findSeasons() {
        return betofficeBasicJsonService.findAllSeason();
    }

    @CrossOrigin
    @RequestMapping(value = "/season", method = RequestMethod.POST, headers = { "Content-type=application/json" })
    public SeasonJson addSeason(@RequestBody SeasonJson season,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminJsonService.validateAdminSession(token);
        return betofficeAdminJsonService.addSeason(season);
    }

    @CrossOrigin
    @RequestMapping(value = "/season", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
    public SeasonJson updateSeason(@RequestBody SeasonJson season,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminJsonService.validateAdminSession(token);
        return betofficeAdminJsonService.updateSeason(season);
    }

    // -- season-group-team administration ------------------------------------

    @CrossOrigin
    @PostMapping(value = "/season/{seasonId}/group")
    public SeasonJson addGroupToSeason(@PathVariable("seasonId") Long seasonId,
            @RequestBody GroupTypeJson groupTypeJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminJsonService.validateAdminSession(token);
        SeasonJson seasonJson = betofficeBasicJsonService.findSeasonById(seasonId);
        betofficeAdminJsonService.addGroupToSeason(seasonJson, groupTypeJson);

        return seasonJson;
    }

    @CrossOrigin
    @DeleteMapping(value = "/season/{seasonId}/group/{groupTypeId}")
    public SeasonJson removeGroupFromSeason(@PathVariable("seasonId") Long seasonId,
            @PathVariable("groupTypeId") Long groupTypeId,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminJsonService.validateAdminSession(token);
        SeasonJson seasonJson = betofficeBasicJsonService.findSeasonById(seasonId);
        GroupTypeJson groupTypeJson = betofficeAdminJsonService.findGroupType(groupTypeId);
        betofficeAdminJsonService.removeGroupFromSeason(seasonJson, groupTypeJson);

        return seasonJson;
    }

    @CrossOrigin
    @GetMapping(value = "/season/{seasonId}/groupteam")
    public SeasonGroupTeamJson findGroupWithTeams(@PathVariable("seasonId") Long seasonId) {
        return betofficeAdminJsonService.findSeasonGroupsAndTeams(seasonId);
    }

    @CrossOrigin
    @GetMapping(value = "/season/{seasonId}/groupteam/{groupTypeId}/candidates")
    public List<TeamJson> findTeamsForAdding(@PathVariable("seasonId") Long seasonId,
            @PathVariable("groupTypeId") Long groupTypeId) {
        SeasonJson seasonJson = betofficeBasicJsonService.findSeasonById(seasonId);
        GroupTypeJson groupTypeJson = betofficeAdminJsonService.findGroupType(groupTypeId);
        return betofficeAdminJsonService.findSeasonGroupAndTeamCandidates(seasonJson, groupTypeJson);
    }

    @CrossOrigin
    @PostMapping(value = "/season/{seasonId}/groupteam/{groupTypeId}")
    public SeasonJson addTeamToGroup(@PathVariable("seasonId") Long seasonId,
            @PathVariable("groupTypeId") Long groupTypeId,
            @RequestBody TeamJson teamJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminJsonService.validateAdminSession(token);
        SeasonJson seasonJson = betofficeBasicJsonService.findSeasonById(seasonId);
        GroupTypeJson groupTypeJson = betofficeAdminJsonService.findGroupType(groupTypeId);
        betofficeAdminJsonService.addTeamToGroup(seasonJson, groupTypeJson, teamJson);
        return seasonJson;
    }

    @CrossOrigin
    @DeleteMapping(value = "/season/{seasonId}/groupteam/{groupTypeId}/team/{teamId}")
    public SeasonJson removeTeamFromGroup(@PathVariable("seasonId") Long seasonId,
            @PathVariable("groupTypeId") Long groupTypeId,
            @PathVariable("teamId") Long teamId,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminJsonService.validateAdminSession(token);
        SeasonJson seasonJson = betofficeBasicJsonService.findSeasonById(seasonId);
        GroupTypeJson groupTypeJson = betofficeAdminJsonService.findGroupType(groupTypeId);
        TeamJson teamJson = betofficeAdminJsonService.findTeam(teamId);
        betofficeAdminJsonService.removeTeamFromGroup(seasonJson, groupTypeJson, teamJson);
        return seasonJson;
    }

    // -- user administration -------------------------------------------------

    @GetMapping(value = "/user/{userId}", headers = { "Content-type=application/json" })
    public PartyJson findUser(@PathVariable("userId") Long userId) {
        return betofficeAdminJsonService.findUser(userId);
    }

    @GetMapping(value = "/user", headers = { "Content-type=application/json" })
    public List<PartyJson> findUsers() {
        return betofficeAdminJsonService.findUsers();
    }

    @PostMapping(value = "/user/add", headers = { "Content-type=application/json" })
    public PartyJson addUser(@RequestBody PartyJson partyJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        betofficeAdminJsonService.validateAdminSession(token);
        return betofficeAdminJsonService.addUser(partyJson);
    }

    @PostMapping(value = "/user/update", headers = { "Content-type=application/json" })
    public PartyJson updateUser(@RequestBody PartyJson partyJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        betofficeAdminJsonService.validateAdminSession(token);
        return betofficeAdminJsonService.updateUser(partyJson);
    }

    // -- team administration -------------------------------------------------

    @RequestMapping(value = "/team/{teamId}", method = RequestMethod.GET, headers = { "Content-type=application/json" })
    public TeamJson findTeam(@PathVariable("teamId") Long teamId) {
        return betofficeAdminJsonService.findTeam(teamId);
    }

    @RequestMapping(value = "/team-search", method = RequestMethod.GET, headers = { "Content-type=application/json" })
    public List<TeamJson> findTeams(
            @RequestParam("filter") String teamFilter,
            @RequestParam(name = "type", required = false) TeamType teamType) {
        return betofficeAdminJsonService.findTeams(Optional.ofNullable(teamType), teamFilter);
    }

    @RequestMapping(value = "/team", method = RequestMethod.GET, headers = { "Content-type=application/json" })
    public List<TeamJson> findTeams() {
        return betofficeAdminJsonService.findTeams();
    }

    @PostMapping(value = "/team", headers = { "Content-type=application/json" })
    public TeamJson addTeam(@RequestBody TeamJson teamJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        betofficeAdminJsonService.validateAdminSession(token);
        return betofficeAdminJsonService.addTeam(teamJson);
    }

    @PutMapping(value = "/team", headers = { "Content-type=application/json" })
    public TeamJson updateTeam(@RequestBody TeamJson teamJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        betofficeAdminJsonService.validateAdminSession(token);
        return betofficeAdminJsonService.updateTeam(teamJson);
    }

    // -- group type administration ------------------------------------------

    @RequestMapping(value = "/groupType", method = RequestMethod.GET, headers = { "Content-type=application/json" })
    public List<GroupTypeJson> listGroupTypes() {
        return betofficeAdminJsonService.findGroupTypes();
    }

    // -- user/season administration ------------------------------------------

    @RequestMapping(value = "/season/{seasonId}/potentialuser", method = RequestMethod.GET, headers = {
            "Content-type=application/json" })
    public List<SeasonMemberJson> listPotentialUsers(@PathVariable("seasonId") Long seasonId) {
        return betofficeAdminJsonService.findPotentialSeasonMembers(seasonId);
    }

    @GetMapping(value = "/season/{seasonId}/user", headers = { "Content-type=application/json" })
    public List<SeasonMemberJson> listUsers(@PathVariable("seasonId") Long seasonId) {
        return betofficeAdminJsonService.findAllSeasonMembers(seasonId);
    }

    @PostMapping(value = "/season/{seasonId}/user/add", headers = { "Content-type=application/json" })
    public List<SeasonMemberJson> addUsers(@PathVariable("seasonId") Long seasonId,
            @RequestBody List<SeasonMemberJson> members,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        betofficeAdminJsonService.validateAdminSession(token);
        return betofficeAdminJsonService.addSeasonMembers(seasonId, members);
    }

    @PostMapping(value = "/season/{seasonId}/user/remove", headers = { "Content-type=application/json" })
    public List<SeasonMemberJson> removeUsers(@PathVariable("seasonId") Long seasonId,
            @RequestBody List<SeasonMemberJson> members,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        betofficeAdminJsonService.validateAdminSession(token);
        return betofficeAdminJsonService.removeSeasonMembers(seasonId, members);
    }

    /**
     * Convert a predefined exception to an HTTP Status code
     */
    @ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Access denied")
    @ExceptionHandler(AccessDeniedException.class)
    public void forbidden() {
    }

}
