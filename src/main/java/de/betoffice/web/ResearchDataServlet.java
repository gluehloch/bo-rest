/*
 * ============================================================================
 * Project betoffice-jweb
 * Copyright (c) 2000-2017 by Andre Winkler. All rights reserved.
 * ============================================================================
 *          GNU GENERAL PUBLIC LICENSE
 *  TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package de.betoffice.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.betoffice.web.json.HistoryTeamVsTeamJson;
import de.betoffice.web.json.HistoryTeamVsTeamJsonMapper;
import de.betoffice.web.json.TeamJson;
import de.winkler.betoffice.service.MasterDataManagerService;
import de.winkler.betoffice.service.SeasonManagerService;
import de.winkler.betoffice.storage.Game;
import de.winkler.betoffice.storage.Team;
import de.winkler.betoffice.storage.enums.TeamType;

/**
 * Researching data...
 * 
 * @author Andre Winkler
 */
@Controller
@RequestMapping("/research")
public class ResearchDataServlet {

	private SeasonManagerService seasonManagerService;

	@Autowired
	public void setSeasonManagerService(SeasonManagerService _seasonManagerService) {
		seasonManagerService = _seasonManagerService;
	}

	private MasterDataManagerService masterDataManagerService;

	@Autowired
	public void setMasterDataManagerService(MasterDataManagerService _masterDataManagerService) {
		masterDataManagerService = _masterDataManagerService;
	}

	// ------------------------------------------------------------------------

	@CrossOrigin
	@RequestMapping(value = "/findDfbTeams", method = RequestMethod.GET)
	public @ResponseBody List<TeamJson> findDfbTeams() {
		List<Team> dfbTeams = masterDataManagerService.findTeams(TeamType.DFB);
		return JsonBuilder.toJsonWithTeams(dfbTeams);
	}

	@CrossOrigin
	@RequestMapping(value = "/findFifaTeams", method = RequestMethod.GET)
	public @ResponseBody List<TeamJson> findFifaTeams() {
		List<Team> fifaTeams = masterDataManagerService.findTeams(TeamType.FIFA);
		return JsonBuilder.toJsonWithTeams(fifaTeams);
	}

	@CrossOrigin
	@RequestMapping(value = "/teamvsteam", method = RequestMethod.GET)
	public @ResponseBody HistoryTeamVsTeamJson research(
			@RequestParam(value = "homeTeam", required = true) long homeTeamId,
			@RequestParam(value = "guestTeam", required = true) long guestTeamId) {

		Team homeTeam = masterDataManagerService.findTeamById(homeTeamId);
		Team guestTeam = masterDataManagerService.findTeamById(guestTeamId);

		List<Game> findMatches = seasonManagerService.findMatches(homeTeam, guestTeam);

		return HistoryTeamVsTeamJsonMapper.map(findMatches);
	}

}
