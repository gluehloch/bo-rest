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

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.betoffice.web.json.RoundJson;
import de.betoffice.web.json.SeasonMemberJson;
import de.winkler.betoffice.service.SecurityToken;

/**
 * The administration part of the betoffice.
 * 
 * <pre>
 * /season/{seasonId}/add          ??? DO I NEED THIS ???
 * /season/{seasonId}/update       ??? DO I NEED THIS ???
 * 
 * /season/{seasonId}/potentialuser Potentielle Tipp-Teilnehmer
 * /season/{seasonId}/user          Aufistung aller Tipp-Teilnehmer 
 * /season/{seasonId}/user/add      Ergänzt die Liste der Tipp-Teilnehmer um einen Teilnehmer
 * /season/{seasonId}/user/remove   Entfernt aus der Liste der Tipp-Teilnehmer einen Teilnehmer
 * 
 * /season/{seasonId}/matchday/list   Auflistung aller Spieltage einer Meisterschaft
 * /season/{seasonId}/matchday/add    Ergänzt die Meisterschaft um einen Spieltag
 * /season/{seasonId}/matchday/remove Entfernt einen Spieltag aus der Meisterschaft
 * /season/{seasonId}/matchday/update Ändert die Daten eines Spieltags
 * 
 * /season/{seasonId}/matchday/{matchday}/game/list   Auflistung aller Spiele eines Spieltages
 * /season/{seasonId}/matchday/{matchday}/game/add    Ergänzt den Spieltag um ein Spiel
 * /season/{seasonId}/matchday/{matchday}/game/remove Entfernt ein Spiel aus dem Spieltag
 * /season/{seasonId}/matchday/{matchday}/game/update Ändert die Daten eines Spiels
 * </pre>
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

        RoundJson roundJson = null; // betofficeAdminJsonService.reconcileRoundWithOpenligadb(token,
                                    // roundId);

        return roundJson;
    }

    // -- user administration -------------------------------------------------

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
            @RequestBody List<SeasonMemberJson> members) {

        return betofficeAdminJsonService.addSeasonMembers(seasonId, members);
    }

    @CrossOrigin
    @RequestMapping(value = "/season/{seasonId}/user/remove", method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public List<SeasonMemberJson> removeUsers(
            @PathVariable("seasonId") Long seasonId,
            @RequestBody List<SeasonMemberJson> members) {

        return betofficeAdminJsonService.removeSeasonMembers(seasonId, members);
    }

}
