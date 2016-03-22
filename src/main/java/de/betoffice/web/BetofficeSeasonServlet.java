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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.betoffice.web.http.ResponseHeaderSetup;
import de.betoffice.web.json.RoundJson;
import de.betoffice.web.json.SeasonJson;
import de.betoffice.web.json.SecurityTokenJson;
import de.betoffice.web.json.TeamJson;
import de.betoffice.web.json.TippRoundJson;
import de.winkler.betoffice.service.SecurityToken;

/**
 * Controller
 */
@Controller
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

    // -----------------------------------------------------------------------

    @RequestMapping(value = "/season/all", method = RequestMethod.GET)
    public @ResponseBody List<SeasonJson> findAllSeason(
            HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);
        return betofficeBasicJsonService.findAllSeason();
    }

    @RequestMapping(value = "/season/{seasonId}", method = RequestMethod.GET)
    public @ResponseBody SeasonJson findSeasonById(
            @PathVariable("seasonId") String seasonId,
            HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);
        return betofficeBasicJsonService.findSeasonById(seasonId);
    }

    /**
     * Return the current (next to play and/or to tipp) round of a season.
     *
     * @param seasonId
     *            Season ID
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @return The current round of a season
     */
    @RequestMapping(value = "/season/{seasonId}/current", method = RequestMethod.GET)
    public @ResponseBody RoundJson findNextTipp(
            @PathVariable("seasonId") Long seasonId,
            HttpServletRequest request, HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);
        return betofficeBasicJsonService.findTippRound(seasonId);
    }

    @RequestMapping(value = "/season/round/{roundId}", method = RequestMethod.GET)
    public @ResponseBody RoundJson findRound(
            @PathVariable("roundId") Long roundId, HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);
        return betofficeBasicJsonService.findRound(roundId);
    }

    @RequestMapping(value = "/season/round/{roundId}/next", method = RequestMethod.GET)
    public @ResponseBody RoundJson findNextRound(
            @PathVariable("roundId") Long roundId, HttpServletRequest request,
            HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);
        return betofficeBasicJsonService.findNextRound(roundId);
    }

    @RequestMapping(value = "/season/round/{roundId}/prev", method = RequestMethod.GET)
    public @ResponseBody RoundJson findPrevRound(
            @PathVariable("roundId") Long roundId, HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);
        return betofficeBasicJsonService.findPrevRound(roundId);
    }

    /**
     * Returns the tipp of a user for a round.
     *
     * @param roundId
     *            Round ID
     * @param nickName
     *            nick name
     * @param response
     *            Servlet response
     * @return The tipp of a user for a round
     */
    @RequestMapping(value = "/tipp/{roundId}/{nickName}", method = RequestMethod.GET)
    public @ResponseBody RoundJson findTipp(
            @PathVariable("roundId") Long roundId,
            @PathVariable("nickName") String nickName,
            HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);
        return betofficeBasicJsonService.findTipp(roundId, nickName);
    }

    /**
     * The current round to tipp
     *
     * @param seasonId
     *            Season ID
     * @param request
     *            Servlet request
     * @param response
     *            Servlet response
     * @return The current tipp
     */
    @RequestMapping(value = "/tipp/{seasonId}/{nickname}/current", method = RequestMethod.GET)
    public @ResponseBody RoundJson findCurrentTipp(
            @PathVariable("seasonId") Long seasonId,
            @PathVariable("nickname") String nickName,
            HttpServletRequest request, HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);
        return betofficeBasicJsonService.findCurrentTipp(seasonId, nickName);
    }

    /**
     * The next tipp ahead of <code>roundId</code>
     *
     * @param roundId
     *            Round ID
     * @param nickName
     *            nick name
     * @param response
     *            Servlet Response
     * @return The next tipp ahead of <code>roundId</code>
     */
    @RequestMapping(value = "/tipp/{roundId}/{nickName}/next", method = RequestMethod.GET)
    public @ResponseBody RoundJson findNextTipp(
            @PathVariable("roundId") Long roundId,
            @PathVariable("nickName") String nickName,
            HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);
        return betofficeBasicJsonService.findNextTipp(roundId, nickName);
    }

    /**
     * The previous tipp behind of <code>roundId</code>
     *
     * @param roundId
     *            Round ID
     * @param nickName
     *            nick name
     * @param response
     *            Servlet Response
     * @return The previous tipp behind of <code>roundId</code>
     */
    @RequestMapping(value = "/tipp/{roundId}/{nickName}/prev", method = RequestMethod.GET)
    public @ResponseBody RoundJson findPrevTipp(
            @PathVariable("roundId") Long roundId,
            @PathVariable("nickName") String nickName,
            HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);
        return betofficeBasicJsonService.findPrevTipp(roundId, nickName);
    }

    @RequestMapping(value = "/team/all", method = RequestMethod.GET)
    public @ResponseBody List<TeamJson> findAllTeams(
            HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);
        return betofficeBasicJsonService.findAllTeams();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, headers = { "Content-type=application/json" })
    public @ResponseBody SecurityTokenJson login(
            @RequestBody AuthenticationForm authenticationForm,
            HttpServletRequest request, HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);
        String userAgent = request.getHeader("User-Agent");

        SecurityTokenJson securityToken = betofficeBasicJsonService.login(
                authenticationForm.getNickname(),
                authenticationForm.getPassword(), request.getSession().getId(),
                request.getRemoteAddr(), userAgent);

        HttpSession session = request.getSession();
        session.setAttribute(SecurityToken.class.getName(), securityToken);

        return securityToken;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST, headers = { "Content-type=application/json" })
    public void logout(@RequestBody LogoutFormData logoutFormData,
            HttpServletRequest request, HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);

        betofficeBasicJsonService.logout(logoutFormData.getNickname(),
                logoutFormData.getToken());

        HttpSession session = request.getSession();
        session.removeAttribute(SecurityToken.class.getName());
    }

    @RequestMapping(value = "/tipp/submit", method = RequestMethod.POST, headers = { "Content-type=application/json" })
    public @ResponseBody RoundJson submitTipp(
            @RequestBody TippRoundJson tippRoundJson, HttpServletRequest request,
            HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);

        return betofficeBasicJsonService.submitTipp(tippRoundJson);
    }

}
