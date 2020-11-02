/*
 * ============================================================================
 * Project betoffice-jweb Copyright (c) 2015 by Andre Winkler. All rights
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

package de.betoffice.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.betoffice.web.json.GameJson;
import de.betoffice.web.json.PartyJson;
import de.betoffice.web.json.RoundAndTableJson;
import de.betoffice.web.json.RoundJson;
import de.betoffice.web.json.SeasonJson;
import de.betoffice.web.json.SeasonMemberJson;
import de.betoffice.web.json.TeamJson;

/**
 * The administration part of the betoffice.
 * 
 * @author Andre Winkler
 */
@RestController
@RequestMapping("/chiefoperator")
public class AdministrationController {

    // ------------------------------------------------------------------------
    // The beans
    // ------------------------------------------------------------------------

    // -- betofficeBasicJsonService -------------------------------------------

    private BetofficeService betofficeBasicJsonService;

    @Autowired
    public void setBetofficeBasicJsonService(
            BetofficeService _betofficeBasicJsonService) {

        betofficeBasicJsonService = _betofficeBasicJsonService;
    }

    // -- betofficeAdminJsonService -------------------------------------------

    private AdminService betofficeAdminJsonService;

    @Autowired
    public void setBetofficeAdminJsonService(
            AdminService _betofficeAdminJsonService) {

        betofficeAdminJsonService = _betofficeAdminJsonService;
    }

    // ------------------------------------------------------------------------

    @CrossOrigin
    @RequestMapping(value = "/season/round/{roundId}/group/{groupId}/ligadbupdate", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public RoundAndTableJson updateRoundByOpenligaDb(
            @PathVariable("roundId") Long roundId,
            @PathVariable("groupId") Long groupId,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminJsonService.validateAdminSession(token);
        betofficeAdminJsonService.reconcileRoundWithOpenligadb(token, roundId);
        return betofficeBasicJsonService.findRoundTable(roundId, groupId);
    }

    @CrossOrigin
    @RequestMapping(value = "/season/round/{roundId}/group/{groupId}/ligadbcreate", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public RoundAndTableJson createOrUpdateRoundByOpenligaDb(
            @PathVariable("roundId") Long roundId,
            @PathVariable("groupId") Long groupId,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminJsonService.validateAdminSession(token);
        betofficeAdminJsonService.mountRoundWithOpenligadb(token, roundId);
        return betofficeBasicJsonService.findRoundTable(roundId, groupId);
    }

    // ------------------------------------------------------------------------

    @CrossOrigin
    @RequestMapping(value = "/season/round/{roundId}/group/{groupId}/update", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public RoundAndTableJson updateRound(@PathVariable("roundId") Long roundId,
            @PathVariable("groupId") Long groupId,
            @RequestBody RoundJson roundJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminJsonService.validateAdminSession(token);
        betofficeAdminJsonService.updateRound(roundJson);
        return betofficeBasicJsonService.findRoundTable(roundId, groupId);
    }

    @CrossOrigin
    @RequestMapping(value = "/game/update", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public GameJson updateGame(@RequestBody GameJson gameJson,
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
    @RequestMapping(value = "/season/list", method = RequestMethod.GET, headers = {
            "Content-type=application/json" })
    public List<SeasonJson> findSeasons() {
        return betofficeBasicJsonService.findAllSeason();
    }

    @CrossOrigin
    @RequestMapping(value = "/season/add", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public SeasonJson addSeason(@RequestBody SeasonJson season,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminJsonService.validateAdminSession(token);
        return betofficeAdminJsonService.addSeason(season);
    }

    @CrossOrigin
    @RequestMapping(value = "/season/update", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public SeasonJson updateSeason(@RequestBody SeasonJson season) {
        return betofficeAdminJsonService.updateSeason(season);
    }

    @CrossOrigin
    @RequestMapping(value = "/season/create", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public SeasonJson createSeason(@RequestBody SeasonJson season) {
        return betofficeAdminJsonService.addSeason(season);
    }

    // -- user administration -------------------------------------------------

    @CrossOrigin
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET, headers = {
            "Content-type=application/json" })
    public PartyJson findUser(@PathVariable("userId") Long userId) {
        return betofficeAdminJsonService.findUser(userId);
    }

    @CrossOrigin
    @RequestMapping(value = "/user/list", method = RequestMethod.GET, headers = {
            "Content-type=application/json" })
    public List<PartyJson> findUsers() {
        return betofficeAdminJsonService.findUsers();
    }

    @CrossOrigin
    @RequestMapping(value = "/user/add", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public PartyJson addUser(@RequestBody PartyJson partyJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        betofficeAdminJsonService.validateAdminSession(token);
        return betofficeAdminJsonService.addUser(partyJson);
    }

    @CrossOrigin
    @RequestMapping(value = "/user/update", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public PartyJson updateUser(@RequestBody PartyJson partyJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        betofficeAdminJsonService.validateAdminSession(token);
        return betofficeAdminJsonService.updateUser(partyJson);
    }

    // -- team administration -------------------------------------------------

    @CrossOrigin
    @RequestMapping(value = "/team/{teamId}", method = RequestMethod.GET, headers = {
            "Content-type=application/json" })
    public TeamJson findTeam(@PathVariable("teamId") Long teamId) {
        return betofficeAdminJsonService.findTeam(teamId);
    }

    @CrossOrigin
    @RequestMapping(value = "/team/list", method = RequestMethod.GET, headers = {
            "Content-type=application/json" })
    public List<TeamJson> findTeams() {
        return betofficeAdminJsonService.findTeams();
    }

    @CrossOrigin
    @RequestMapping(value = "/team/add", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public TeamJson addTeam(@RequestBody TeamJson teamJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        betofficeAdminJsonService.validateAdminSession(token);
        return betofficeAdminJsonService.addTeam(teamJson);
    }

    @CrossOrigin
    @RequestMapping(value = "/team/update", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public TeamJson updateTeam(@RequestBody TeamJson teamJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        betofficeAdminJsonService.validateAdminSession(token);
        return betofficeAdminJsonService.updateTeam(teamJson);
    }

    // -- user/season administration ------------------------------------------

    @CrossOrigin
    @RequestMapping(value = "/season/{seasonId}/potentialuser", method = RequestMethod.GET, headers = {
            "Content-type=application/json" })
    public List<SeasonMemberJson> listPotentialUsers(
            @PathVariable("seasonId") Long seasonId) {

        return betofficeAdminJsonService.findPotentialSeasonMembers(seasonId);
    }

    @CrossOrigin
    @RequestMapping(value = "/season/{seasonId}/user", method = RequestMethod.GET, headers = {
            "Content-type=application/json" })
    public List<SeasonMemberJson> listUsers(
            @PathVariable("seasonId") Long seasonId) {

        return betofficeAdminJsonService.findAllSeasonMembers(seasonId);
    }

    @CrossOrigin
    @RequestMapping(value = "/season/{seasonId}/user/add", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public List<SeasonMemberJson> addUsers(
            @PathVariable("seasonId") Long seasonId,
            @RequestBody List<SeasonMemberJson> members,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        betofficeAdminJsonService.validateAdminSession(token);
        return betofficeAdminJsonService.addSeasonMembers(seasonId, members);
    }

    @CrossOrigin
    @RequestMapping(value = "/season/{seasonId}/user/remove", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public List<SeasonMemberJson> removeUsers(
            @PathVariable("seasonId") Long seasonId,
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