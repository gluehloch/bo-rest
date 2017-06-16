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

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.betoffice.web.json.RoundJson;
import de.winkler.betoffice.service.SecurityToken;

/**
 * The administration part of the betoffice.
 * 
 * @author Andre Winkler
 */
@RestController
@RequestMapping("/chiefoperator")
public class AdministrationBetofficeServlet {

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
    @RequestMapping(value = "/season/", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public RoundJson updateRound(@PathVariable("roundId") Long roundId,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent,
            HttpSession httpSession) {

        // SecurityTokenJson securityToken = betofficeBasicJsonService.login(
        // authenticationForm.getNickname(),
        // authenticationForm.getPassword(), request.getSession().getId(),
        // request.getRemoteAddr(), userAgent);

        if (httpSession == null) {
            return null;
        }

        Object attribute = httpSession
                .getAttribute(SecurityToken.class.getName());

        RoundJson roundJson = null; //betofficeAdminJsonService.reconcileRoundWithOpenligadb(token, roundId);

        return roundJson;
    }
    
}
