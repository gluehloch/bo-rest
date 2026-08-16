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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.betoffice.storage.group.GroupTypeDto;
import de.betoffice.storage.season.AddRoundJson;
import de.betoffice.storage.season.GameDto;
import de.betoffice.storage.season.RoundDto;
import de.betoffice.storage.season.SeasonDto;
import de.betoffice.storage.season.UpdateRoundDto;
import de.betoffice.storage.season.entity.RoundAndTableJson;
import de.betoffice.storage.season.entity.SeasonGroupTeamJson;
import de.betoffice.storage.season.entity.SeasonMemberJson;
import de.betoffice.storage.team.TeamDto;
import de.betoffice.storage.team.TeamType;
import de.betoffice.storage.user.PartyDto;
import de.betoffice.validation.ValidationMessages;
import de.betoffice.web.BetofficeHttpConsts;
import de.betoffice.web.season.BetofficeService;

/**
 * The administration part of the betoffice.
 * 
 * @author Andre Winkler
 */
@CrossOrigin
@RestController
@RequestMapping("/chiefoperator")
public class AdministrationController {

    @Autowired
    private BetofficeService betofficeService;

    @Autowired
    private AdminService betofficeAdminService;

    // -- openligadb ----------------------------------------------------------

    @PreAuthorize("@authService.isAdminSession(#token)")
    @PostMapping(value = "/season/{seasonId}/round/{roundId}/group/{groupId}/ligadbupdate", headers = {
            "Content-type=application/json" })
    public RoundAndTableJson updateRoundByOpenligaDb(
            @PathVariable("seasonId") Long seasonId,
            @PathVariable("roundId") Long roundId,
            @PathVariable("groupId") Long groupId,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminService.validateAdminSession(token);
        betofficeAdminService.reconcileRoundWithOpenligadb(token, seasonId, roundId);
        return betofficeService.findRoundTable(seasonId, roundId, groupId);
    }

    @PreAuthorize("@authService.isAdminSession(#token)")
    @PostMapping(value = "/season/{seasonId}/round/{roundId}/group/{groupId}/ligadbcreate", headers = {
            "Content-type=application/json" })
    public RoundAndTableJson createOrUpdateRoundByOpenligaDb(
            @PathVariable("seasonId") Long seasonId,
            @PathVariable("roundId") Long roundId,
            @PathVariable("groupId") Long groupId,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminService.validateAdminSession(token);
        betofficeAdminService.mountRoundWithOpenligadb(token, seasonId, roundId);
        return betofficeService.findRoundTable(seasonId, roundId, groupId);
    }

    // -- round update --------------------------------------------------------

    @PreAuthorize("@authService.isAdminSession(#token)")
    @PostMapping(value = "/season/{seasonId}/round/{roundId}/group/{groupId}/update", headers = {
            "Content-type=application/json" })
    public RoundAndTableJson updateRoundAndGames(
            @PathVariable("seasonId") Long seasonId,
            @PathVariable("roundId") Long roundId,
            @PathVariable("groupId") Long groupId,
            @RequestBody RoundDto roundJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminService.validateAdminSession(token);
        betofficeAdminService.updateRoundAndGames(seasonId, roundId, roundJson);
        return betofficeService.findRoundTable(seasonId, roundId, groupId);
    }

    @PreAuthorize("@authService.isAdminSession(#token)")
    @PutMapping(value = "/season/{seasonId}/round/{roundId}", headers = { "Content-type=application/json" })
    public ResponseEntity<Void> updateRound(
            @PathVariable("seasonId") Long seasonId,
            @PathVariable("roundId") Long roundId,
            @RequestBody UpdateRoundDto updateRoundJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminService.validateAdminSession(token);
        ValidationMessages updateRound = betofficeAdminService.updateRound(seasonId, roundId, updateRoundJson);

        // TODO 
        // ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NO_CONTENT, "Round updated successfully"));

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@authService.isAdminSession(#token)")
    @PostMapping(value = "/season/{seasonId}/round", headers = { "Content-type=application/json" })
    public ResponseEntity<Void> addRound(
            @PathVariable("seasonId") Long seasonId,
            @RequestBody AddRoundJson addRoundJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminService.validateAdminSession(token);
        ValidationMessages updateRound = betofficeAdminService.addRound(seasonId, addRoundJson);

        // TODO 
        // ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NO_CONTENT, "Round updated successfully"));

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@authService.isAdminSession(#token)")
    @PostMapping(value = "/game/update", headers = { "Content-type=application/json" })
    public GameDto updateGame(@RequestBody GameDto gameJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminService.validateAdminSession(token);
        betofficeAdminService.updateGame(gameJson);
        return betofficeService.findGame(gameJson.getId());
    }

    // -- season administration -----------------------------------------------

    @CrossOrigin
    @GetMapping(value = "/season/{seasonId}", headers = { "Content-type=application/json" })
    public SeasonDto findSeason(@PathVariable("seasonId") Long seasonId) {
        return betofficeService.findSeasonById(seasonId);
    }

    @CrossOrigin
    @GetMapping(value = "/season", headers = { "Content-type=application/json" })
    public List<SeasonDto> findSeasons() {
        return betofficeService.findAllSeason();
    }

    @PreAuthorize("@authService.isAdminSession(#token)")
    @CrossOrigin
    @RequestMapping(value = "/season", method = RequestMethod.POST, headers = { "Content-type=application/json" })
    public SeasonDto addSeason(@RequestBody SeasonDto season,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminService.validateAdminSession(token);
        return betofficeAdminService.addSeason(season);
    }

    @PreAuthorize("@authService.isAdminSession(#token)")
    @CrossOrigin
    @PutMapping(value = "/season", headers = { "Content-type=application/json" })
    public SeasonDto updateSeason(@RequestBody SeasonDto season,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminService.validateAdminSession(token);
        return betofficeAdminService.updateSeason(season);
    }

    // -- season-group-team administration ------------------------------------

    @PreAuthorize("@authService.isAdminSession(#token)")
    @CrossOrigin
    @PostMapping(value = "/season/{seasonId}/group")
    public SeasonDto addGroupToSeason(@PathVariable("seasonId") Long seasonId,
            @RequestBody GroupTypeDto groupTypeJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminService.validateAdminSession(token);
        SeasonDto seasonJson = betofficeService.findSeasonById(seasonId);
        betofficeAdminService.addGroupToSeason(seasonJson, groupTypeJson);

        return seasonJson;
    }

    @PreAuthorize("@authService.isAdminSession(#token)")
    @CrossOrigin
    @DeleteMapping(value = "/season/{seasonId}/group/{groupTypeId}")
    public SeasonDto removeGroupFromSeason(@PathVariable("seasonId") Long seasonId,
            @PathVariable("groupTypeId") Long groupTypeId,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminService.validateAdminSession(token);
        SeasonDto seasonJson = betofficeService.findSeasonById(seasonId);
        GroupTypeDto groupTypeJson = betofficeAdminService.findGroupType(groupTypeId);
        betofficeAdminService.removeGroupFromSeason(seasonJson, groupTypeJson);

        return seasonJson;
    }

    @CrossOrigin
    @GetMapping(value = "/season/{seasonId}/groupteam")
    public SeasonGroupTeamJson findGroupWithTeams(@PathVariable("seasonId") Long seasonId) {
        return betofficeAdminService.findSeasonGroupsAndTeams(seasonId);
    }

    @CrossOrigin
    @GetMapping(value = "/season/{seasonId}/groupteam/{groupTypeId}/candidates")
    public List<TeamDto> findTeamsForAdding(@PathVariable("seasonId") Long seasonId,
            @PathVariable("groupTypeId") Long groupTypeId) {
        SeasonDto seasonJson = betofficeService.findSeasonById(seasonId);
        GroupTypeDto groupTypeJson = betofficeAdminService.findGroupType(groupTypeId);
        return betofficeAdminService.findSeasonGroupAndTeamCandidates(seasonJson, groupTypeJson);
    }

    @PreAuthorize("@authService.isAdminSession(#token)")
    @CrossOrigin
    @PostMapping(value = "/season/{seasonId}/groupteam/{groupTypeId}")
    public SeasonDto addTeamToGroup(@PathVariable("seasonId") Long seasonId,
            @PathVariable("groupTypeId") Long groupTypeId,
            @RequestBody TeamDto teamJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminService.validateAdminSession(token);
        SeasonDto seasonJson = betofficeService.findSeasonById(seasonId);
        GroupTypeDto groupTypeJson = betofficeAdminService.findGroupType(groupTypeId);
        betofficeAdminService.addTeamToGroup(seasonJson, groupTypeJson, teamJson);
        return seasonJson;
    }

    @PreAuthorize("@authService.isAdminSession(#token)")
    @CrossOrigin
    @DeleteMapping(value = "/season/{seasonId}/groupteam/{groupTypeId}/team/{teamId}")
    public SeasonDto removeTeamFromGroup(@PathVariable("seasonId") Long seasonId,
            @PathVariable("groupTypeId") Long groupTypeId,
            @PathVariable("teamId") Long teamId,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        betofficeAdminService.validateAdminSession(token);
        SeasonDto seasonJson = betofficeService.findSeasonById(seasonId);
        GroupTypeDto groupTypeJson = betofficeAdminService.findGroupType(groupTypeId);
        TeamDto teamJson = betofficeAdminService.findTeam(teamId);
        betofficeAdminService.removeTeamFromGroup(seasonJson, groupTypeJson, teamJson);
        return seasonJson;
    }

    // -- user administration -------------------------------------------------

    @GetMapping(value = "/user/{userId}", headers = { "Content-type=application/json" })
    public PartyDto findUser(@PathVariable("userId") Long userId) {
        return betofficeAdminService.findUser(userId);
    }

    @GetMapping(value = "/user", headers = { "Content-type=application/json" })
    public List<PartyDto> findUsers() {
        return betofficeAdminService.findUsers();
    }

    @PreAuthorize("@authService.isAdminSession(#token)")
    @PostMapping(value = "/user/add", headers = { "Content-type=application/json" })
    public PartyDto addUser(@RequestBody PartyDto partyJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        betofficeAdminService.validateAdminSession(token);
        return betofficeAdminService.addUser(partyJson);
    }

    @PostMapping(value = "/user/update", headers = { "Content-type=application/json" })
    public PartyDto updateUser(@RequestBody PartyDto partyJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        betofficeAdminService.validateAdminSession(token);
        return betofficeAdminService.updateUser(partyJson);
    }

    // -- team administration -------------------------------------------------

    @GetMapping(value = "/team/{teamId}", headers = { "Content-type=application/json" })
    public TeamDto findTeam(@PathVariable("teamId") Long teamId) {
        return betofficeAdminService.findTeam(teamId);
    }

    @GetMapping(value = "/team-search", headers = { "Content-type=application/json" })
    public List<TeamDto> findTeams(
            @RequestParam(name = "filter", required = false) String teamFilter,
            @RequestParam(name = "type", required = false) TeamType teamType) {
        return betofficeAdminService.findTeams(Optional.ofNullable(teamType), teamFilter);
    }

    @GetMapping(value = "/team", headers = { "Content-type=application/json" })
    public List<TeamDto> findTeams() {
        return betofficeAdminService.findTeams();
    }

    @PreAuthorize("@authService.isAdminSession(#token)")
    @PostMapping(value = "/team", headers = { "Content-type=application/json" })
    public TeamDto addTeam(@RequestBody TeamDto teamJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        betofficeAdminService.validateAdminSession(token);
        return betofficeAdminService.addTeam(teamJson);
    }

    @PreAuthorize("@authService.isAdminSession(#token)")
    @PutMapping(value = "/team", headers = { "Content-type=application/json" })
    public TeamDto updateTeam(@RequestBody TeamDto teamJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        betofficeAdminService.validateAdminSession(token);
        return betofficeAdminService.updateTeam(teamJson);
    }

    // -- group type administration ------------------------------------------

    @GetMapping(value = "/groupType", headers = { "Content-type=application/json" })
    public List<GroupTypeDto> listGroupTypes() {
        return betofficeAdminService.findGroupTypes();
    }

    // -- user/season administration ------------------------------------------

    @GetMapping(value = "/season/{seasonId}/potentialuser", headers = { "Content-type=application/json" })
    public List<SeasonMemberJson> listPotentialUsers(@PathVariable("seasonId") Long seasonId) {
        return betofficeAdminService.findPotentialSeasonMembers(seasonId);
    }

    @GetMapping(value = "/season/{seasonId}/user", headers = { "Content-type=application/json" })
    public List<SeasonMemberJson> listUsers(@PathVariable("seasonId") Long seasonId) {
        return betofficeAdminService.findAllSeasonMembers(seasonId);
    }

    @PreAuthorize("@authService.isAdminSession(#token)")
    @PostMapping(value = "/season/{seasonId}/user/add", headers = { "Content-type=application/json" })
    public List<SeasonMemberJson> addUsers(@PathVariable("seasonId") Long seasonId,
            @RequestBody List<SeasonMemberJson> members,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        betofficeAdminService.validateAdminSession(token);
        return betofficeAdminService.addSeasonMembers(seasonId, members);
    }

    @PreAuthorize("@authService.isAdminSession(#token)")
    @PostMapping(value = "/season/{seasonId}/user/remove", headers = { "Content-type=application/json" })
    public List<SeasonMemberJson> removeUsers(@PathVariable("seasonId") Long seasonId,
            @RequestBody List<SeasonMemberJson> members,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        betofficeAdminService.validateAdminSession(token);
        return betofficeAdminService.removeSeasonMembers(seasonId, members);
    }

}
