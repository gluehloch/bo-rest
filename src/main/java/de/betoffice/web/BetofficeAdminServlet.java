/*
 * ============================================================================
 * Project betoffice-jweb Copyright (c) 2015-2016 by Andre Winkler. All rights
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

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.betoffice.service.OpenligadbUpdateService;
import de.betoffice.web.BetofficeBasicJsonService;
import de.betoffice.web.admin.json.CreateSeasonJson;
import de.betoffice.web.admin.json.UpdateRoundJson;
import de.betoffice.web.http.ResponseHeaderSetup;
import de.betoffice.web.json.JsonBuilder;
import de.betoffice.web.json.RoundJson;
import de.betoffice.web.json.SeasonJson;
import de.betoffice.web.json.ServiceResponseMessage;
import de.betoffice.web.json.ServiceResponseMessage.Status;
import de.betoffice.web.json.TeamJson;
import de.winkler.betoffice.service.MasterDataManagerService;
import de.winkler.betoffice.service.SeasonManagerService;
import de.winkler.betoffice.storage.GameList;
import de.winkler.betoffice.storage.Season;
import de.winkler.betoffice.storage.enums.SeasonType;
import de.winkler.betoffice.storage.enums.TeamType;
import de.winkler.betoffice.validation.BetofficeValidationException;

/**
 * Controller for the admin view.
 *
 * This should be a REST API
 *
 * <pre>
 *     POST /betoffice/season/login param={username, password}
 *     X POST /betoffice/season/find_season Param={seasonId, roundId, gameId}
 *
 *     GET  /betoffice/admin/season/{seasonId}
 *     GET  /betoffice/admin/season/all
 *     GET  /betoffice/admin/season/{seasonId}/round/{roundId}
 *     GET  /betoffice/admin/season/{seasonId}/round/all
 *     GET  /betoffice/admin/season/{seasonId}/round/{roundId}/game/{gameId}
 *     GET  /betoffice/admin/season/{seasonId}/round/{roundId}/game/all
 *
 *     POST
 * </pre>
 *
 */
@Controller
@RequestMapping("/admin")
public class BetofficeAdminServlet {

    // ------------------------------------------------------------------------
    // The beans
    // ------------------------------------------------------------------------

    // -- dfbDownloadService --------------------------------------------------

    private OpenligadbUpdateService openligadbUpdateService;

    @Autowired
    public void setOpenligadbUpdateService(
            OpenligadbUpdateService _openligadbUpdateService) {

        openligadbUpdateService = _openligadbUpdateService;
    }

    // -- seasonManagerService ------------------------------------------------

    private SeasonManagerService seasonManagerService;

    @Autowired
    public void setSeasonManagerService(
            SeasonManagerService _seasonManagerService) {
        seasonManagerService = _seasonManagerService;
    }

    // -- masterDataManagerService --------------------------------------------

    private MasterDataManagerService masterDataManagerService;

    @Autowired
    public void setMasterDataManagerService(
            MasterDataManagerService _masterDataManagerService) {

        masterDataManagerService = _masterDataManagerService;
    }

    // -- betofficeBasicJsonService -------------------------------------------

    private BetofficeBasicJsonService betofficeBasicJsonService;

    @Autowired
    public void setBetofficeBasicJsonService(
            BetofficeBasicJsonService _betofficeBasicJsonService) {

        betofficeBasicJsonService = _betofficeBasicJsonService;
    }

    // -----------------------------------------------------------------------

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody RoundJson login( HttpServletResponse response) {
        ResponseHeaderSetup.setup(response);
        
        return betofficeBasicJsonService.findRound(roundId);
    }

}
