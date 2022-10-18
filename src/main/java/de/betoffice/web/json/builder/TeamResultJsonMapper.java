/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2017-2020 by Andre Winkler.
 * All rights reserved.
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

package de.betoffice.web.json.builder;

import de.betoffice.web.json.TeamJson;
import de.betoffice.web.json.TeamResultJson;
import de.winkler.betoffice.storage.TeamResult;

/**
 * Mapping from {@link TeamResult} to {@link TeamResultJson}.
 *
 * @author Andre Winkler
 */
public class TeamResultJsonMapper {

    public TeamResultJson map(TeamResult teamResult, TeamResultJson teamResultJson) {
        teamResultJson.setLost(teamResult.getLost());
        teamResultJson.setNegGoals(teamResult.getNegGoals());
        teamResultJson.setPosGoals(teamResult.getPosGoals());
        teamResultJson.setRemis(teamResult.getRemis());
        teamResultJson.setTablePosition(teamResult.getTabPos());
        teamResultJson.setTeam(new TeamJsonMapper().map(teamResult.getTeam(), new TeamJson()));
        teamResultJson.setWin(teamResult.getWin());
        return teamResultJson;
    }

}
