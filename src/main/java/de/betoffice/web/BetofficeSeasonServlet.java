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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.betoffice.web.http.ResponseHeaderSetup;
import de.betoffice.web.json.RoundJson;
import de.betoffice.web.json.SeasonJson;
import de.betoffice.web.json.TeamJson;
import de.winkler.betoffice.service.AuthService;
import de.winkler.betoffice.service.MasterDataManagerService;
import de.winkler.betoffice.service.SeasonManagerService;
import de.winkler.betoffice.service.SecurityToken;
import de.winkler.betoffice.service.TippService;

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

    @RequestMapping(value = "/season/round/{roundId}", method = RequestMethod.GET)
    public @ResponseBody RoundJson findRound(
            @PathVariable("roundId") Long roundId, HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);
        return betofficeBasicJsonService.findRound(roundId);
    }

    @RequestMapping(value = "/season/round/{roundId}/next", method = RequestMethod.GET)
    public @ResponseBody RoundJson findNextRound(
            @PathVariable("roundId") Long roundId, HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);
        return betofficeBasicJsonService.findNextRound(roundId);
    }

    @RequestMapping(value = "/season/round/{roundId}/prev", method = RequestMethod.GET)
    public @ResponseBody RoundJson findPrevRound(
            @PathVariable("roundId") Long roundId, HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);
        return betofficeBasicJsonService.findPrevRound(roundId);
    }

    @RequestMapping(value = "/season/tipp/{roundId}/{nickName}", method = RequestMethod.GET)
    public @ResponseBody RoundJson findTipp(
            @PathVariable("roundId") Long roundId,
            @PathVariable("nickName") String nickName,
            HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);
        return betofficeBasicJsonService.findTipp(roundId, nickName);
    }

    @RequestMapping(value = "/season/{seasonId}/tipp/next", method = RequestMethod.GET)
    public @ResponseBody RoundJson findNextTipp(
            @PathVariable("seasonId") Long seasonId,
            HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);
        return betofficeBasicJsonService.findTippRound(seasonId);
    }

    @RequestMapping(value = "/team/all", method = RequestMethod.GET)
    public @ResponseBody List<TeamJson> findAllTeams(
            HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);
        return betofficeBasicJsonService.findAllTeams();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody SecurityToken auth(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response) {

        ResponseHeaderSetup.setup(response);

        SecurityToken securityToken = betofficeBasicJsonService.login(username, password);

        HttpSession session = request.getSession();
        session.setAttribute(SecurityToken.class.getName(), securityToken);

        if (securityToken.isNotLoggedIn()) {

        } else {

            Cookie[] cookies = request.getCookies();

            for (Cookie cookie : cookies) {
                response.addCookie(cookie);
            }
        }

        return null;
    }

}
