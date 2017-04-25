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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.betoffice.web.json.GroupTypeJson;
import de.betoffice.web.json.RoundAndTableJson;
import de.betoffice.web.json.RoundJson;
import de.betoffice.web.json.SeasonJson;
import de.betoffice.web.json.SecurityTokenJson;
import de.betoffice.web.json.SubmitTippRoundJson;
import de.betoffice.web.json.TeamJson;
import de.betoffice.web.json.TokenJson;
import de.betoffice.web.json.UserTableJson;
import de.winkler.betoffice.service.SecurityToken;

/**
 * Controller
 */
@RestController
@RequestMapping("/office")
public class BetofficeSeasonServlet {

    // ------------------------------------------------------------------------
    // The beans
    // ------------------------------------------------------------------------

    // -- betofficeBasicJsonService -------------------------------------------

    private BetofficeBasicJsonService betofficeBasicJsonService;

    @Autowired
    public void setBetofficeBasicJsonService(
            BetofficeBasicJsonService _betofficeBasicJsonService) {

        betofficeBasicJsonService = _betofficeBasicJsonService;
    }

    // -- betofficeAdminJsonService -------------------------------------------

    private BetofficeAdminJsonService betofficeAdminJsonService;

    @Autowired
    public void setBetofficeAdminJsonService(
            BetofficeAdminJsonService _betofficeAdminJsonService) {

        betofficeAdminJsonService = _betofficeAdminJsonService;
    }

    // ------------------------------------------------------------------------

    @CrossOrigin
    @RequestMapping(value = "/season/all", method = RequestMethod.GET)
    public List<SeasonJson> findAllSeason(HttpServletResponse response) {

        return betofficeBasicJsonService.findAllSeason();
    }

    @CrossOrigin
    @RequestMapping(value = "/season/{seasonId}", method = RequestMethod.GET)
    public SeasonJson findSeasonById(@PathVariable("seasonId") Long seasonId,
            HttpServletResponse response) {

        return betofficeBasicJsonService.findSeasonById(seasonId);
    }

    @CrossOrigin
    @RequestMapping(value = "/season/{seasonId}/group/all", method = RequestMethod.GET)
    public List<GroupTypeJson> findGroupTypes(
            @PathVariable("seasonId") Long seasonId) {

        return betofficeBasicJsonService.findAllGroups(seasonId);
    }

    @CrossOrigin
    @RequestMapping(value = "/season/{seasonId}/group/{groupTypeId}/round/all", method = RequestMethod.GET)
    public SeasonJson findAllRounds(@PathVariable("seasonId") Long seasonId,
            @PathVariable("groupTypeId") Long groupTypeId) {

        return betofficeBasicJsonService.findAllRounds(seasonId, groupTypeId);
    }

    /**
     * Return the current (next to play and/or to tipp) round of a season.
     *
     * @param seasonId
     *            Season ID
     * @return The current round of a season
     */
    @CrossOrigin
    @RequestMapping(value = "/season/{seasonId}/current", method = RequestMethod.GET)
    public RoundJson findNextTipp(@PathVariable("seasonId") Long seasonId) {
        return betofficeBasicJsonService.findTippRound(seasonId);
    }

    //
    // round
    //

    @CrossOrigin
    @RequestMapping(value = "/season/round/{roundId}", method = RequestMethod.GET)
    public RoundJson findRound(@PathVariable("roundId") Long roundId) {
        return betofficeBasicJsonService.findRound(roundId);
    }

    @CrossOrigin
    @RequestMapping(value = "/season/round/{roundId}/next", method = RequestMethod.GET)
    public RoundJson findNextRound(@PathVariable("roundId") Long roundId) {
        return betofficeBasicJsonService.findNextRound(roundId);
    }

    @CrossOrigin
    @RequestMapping(value = "/season/round/{roundId}/prev", method = RequestMethod.GET)
    public RoundJson findPrevRound(@PathVariable("roundId") Long roundId) {
        return betofficeBasicJsonService.findPrevRound(roundId);
    }

    @CrossOrigin
    @RequestMapping(value = "/season/round/{roundId}/update", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public RoundJson updateRound(@PathVariable("roundId") Long roundId,
            @RequestBody TokenJson token, HttpServletRequest request) {

        String userAgent = request.getHeader("User-Agent");

        // SecurityTokenJson securityToken = betofficeBasicJsonService.login(
        // authenticationForm.getNickname(),
        // authenticationForm.getPassword(), request.getSession().getId(),
        // request.getRemoteAddr(), userAgent);

        HttpSession session = request.getSession();
        if (session == null) {
            return null;
        }

        Object attribute = session.getAttribute(SecurityToken.class.getName());

        RoundJson roundJson = betofficeAdminJsonService
                .reconcileRoundWithOpenligadb(token.getToken(), roundId);

        return roundJson;
    }

    @CrossOrigin
    @RequestMapping(value = "/season/round/{roundId}/create", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public RoundJson createOrUpdateRound(@PathVariable("roundId") Long roundId,
            @RequestBody TokenJson token, HttpServletRequest request) {

        String userAgent = request.getHeader("User-Agent");

        // SecurityTokenJson securityToken = betofficeBasicJsonService.login(
        // authenticationForm.getNickname(),
        // authenticationForm.getPassword(), request.getSession().getId(),
        // request.getRemoteAddr(), userAgent);

        HttpSession session = request.getSession();
        if (session == null) {
            return null;
        }

        Object attribute = session.getAttribute(SecurityToken.class.getName());

        RoundJson roundJson = betofficeAdminJsonService
                .mountRoundWithOpenligadb(token.getToken(), roundId);

        return roundJson;
    }

    //
    // round and table
    //

    /*
     * @CrossOrigin
     * 
     * @RequestMapping(value = "/season/{seasonId}/roundtable/current", method =
     * RequestMethod.GET) public RoundAndTableJson
     * findCurrentRoundTable(@PathVariable("seasonId") Long seasonId) {
     * RoundJson currentSeason =
     * betofficeBasicJsonService.findCurrent(seasonId); return null; }
     */

    @CrossOrigin
    @RequestMapping(value = "/season/roundtable/{roundId}/group/{groupTypeId}", method = RequestMethod.GET)
    public RoundAndTableJson findRoundTable(
            @PathVariable("roundId") Long roundId,
            @PathVariable("groupTypeId") Long groupTypeId) {

        return betofficeBasicJsonService.findRoundTable(roundId, groupTypeId);
    }

    // TODO Implement and use me
    @CrossOrigin
    @RequestMapping(value = "/season/roundtable/{roundId}/next", method = RequestMethod.GET)
    public RoundAndTableJson findNextRoundTable(
            @PathVariable("roundId") Long roundId) {

        return betofficeBasicJsonService.findNextRoundTable(roundId);
    }

    // TODO Implement and use me
    @CrossOrigin
    @RequestMapping(value = "/season/roundtable/{roundId}/prev", method = RequestMethod.GET)
    public RoundAndTableJson findPrevRoundTable(
            @PathVariable("roundId") Long roundId) {

        return betofficeBasicJsonService.findPrevRoundTable(roundId);
    }

    //
    // user ranking
    //

    @CrossOrigin
    @RequestMapping(value = "/ranking/season/{seasonId}", method = RequestMethod.GET)
    public UserTableJson findUserTableBySeason(
            @PathVariable("seasonId") Long seasonId) {

        return betofficeBasicJsonService.calcUserRanking(seasonId);
    }

    @CrossOrigin
    @RequestMapping(value = "/ranking/round/{roundId}/", method = RequestMethod.GET)
    public UserTableJson findUserTableByRound(
            @PathVariable("roundId") Long roundId) {

        return betofficeBasicJsonService.calcUserRankingByRound(roundId);
    }

    @CrossOrigin
    @RequestMapping(value = "/ranking/round/{roundId}/next", method = RequestMethod.GET)
    public UserTableJson findUserTableByNextRound(
            @PathVariable("roundId") Long roundId) {

        return betofficeBasicJsonService.calcUserRankingByNextRound(roundId);
    }

    @CrossOrigin
    @RequestMapping(value = "/ranking/round/{roundId}/prev", method = RequestMethod.GET)
    public UserTableJson findUserTableByPrevRound(
            @PathVariable("roundId") Long roundId) {

        return betofficeBasicJsonService.calcUserRankingByPrevRound(roundId);
    }

    //
    // tipp
    //

    /**
     * Returns the tipp of a user for a round.
     *
     * @param roundId
     *            Round ID
     * @param nickName
     *            nick name
     * @return The tipp of a user for a round
     */
    @CrossOrigin
    @RequestMapping(value = "/tipp/{roundId}/{nickName}", method = RequestMethod.GET)
    public RoundJson findTipp(@PathVariable("roundId") Long roundId,
            @PathVariable("nickName") String nickName) {

        return betofficeBasicJsonService.findTipp(roundId, nickName);
    }

    /**
     * The current round to tipp
     *
     * @param seasonId
     *            Season ID
     * @param nickName
     *            Der Name des Users
     * @return The current tipp
     */
    @CrossOrigin
    @RequestMapping(value = "/tipp/{seasonId}/{nickname}/current", method = RequestMethod.GET)
    public RoundJson findCurrentTipp(@PathVariable("seasonId") Long seasonId,
            @PathVariable("nickname") String nickName) {

        return betofficeBasicJsonService.findCurrentTipp(seasonId, nickName);
    }

    /**
     * The next tipp ahead of <code>roundId</code>
     *
     * @param roundId
     *            Round ID
     * @param nickName
     *            nick name
     * @return The next tipp ahead of <code>roundId</code>
     */
    @CrossOrigin
    @RequestMapping(value = "/tipp/{roundId}/{nickName}/next", method = RequestMethod.GET)
    public RoundJson findNextTipp(@PathVariable("roundId") Long roundId,
            @PathVariable("nickName") String nickName) {

        return betofficeBasicJsonService.findNextTipp(roundId, nickName);
    }

    /**
     * The previous tipp behind of <code>roundId</code>
     *
     * @param roundId
     *            Round ID
     * @param nickName
     *            nick name
     * @return The previous tipp behind of <code>roundId</code>
     */
    @CrossOrigin
    @RequestMapping(value = "/tipp/{roundId}/{nickName}/prev", method = RequestMethod.GET)
    public RoundJson findPrevTipp(@PathVariable("roundId") Long roundId,
            @PathVariable("nickName") String nickName) {

        return betofficeBasicJsonService.findPrevTipp(roundId, nickName);
    }

    @CrossOrigin
    @RequestMapping(value = "/team/all", method = RequestMethod.GET)
    public List<TeamJson> findAllTeams() {
        return betofficeBasicJsonService.findAllTeams();
    }

    @CrossOrigin
    @RequestMapping(value = "/login", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public SecurityTokenJson login(
            @RequestBody AuthenticationForm authenticationForm,
            HttpServletRequest request) {

        String userAgent = request.getHeader("User-Agent");

        SecurityTokenJson securityToken = betofficeBasicJsonService.login(
                authenticationForm.getNickname(),
                authenticationForm.getPassword(), request.getSession().getId(),
                request.getRemoteAddr(), userAgent);

        HttpSession session = request.getSession();
        session.setAttribute(SecurityToken.class.getName(), securityToken);

        return securityToken;
    }

    @CrossOrigin
    @RequestMapping(value = "/logout", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public void logout(@RequestBody LogoutFormData logoutFormData,
            HttpServletRequest request) {

        betofficeBasicJsonService.logout(logoutFormData.getNickname(),
                logoutFormData.getToken());

        HttpSession session = request.getSession();
        session.removeAttribute(SecurityToken.class.getName());
    }

    @CrossOrigin
    @RequestMapping(value = "/tipp/submit", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public RoundJson submitTipp(
            @RequestBody SubmitTippRoundJson tippRoundJson) {

        return betofficeBasicJsonService.submitTipp(tippRoundJson);
    }

}
