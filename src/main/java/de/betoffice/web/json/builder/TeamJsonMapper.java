/*
 * ============================================================================
 * Project betoffice-jweb-misc 
 * Copyright (c) 2000-2023 by Andre Winkler. All rights reserved.
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

package de.betoffice.web.json.builder;

import java.util.List;

import de.betoffice.web.json.TeamJson;
import de.winkler.betoffice.storage.Team;
import de.winkler.betoffice.storage.enums.TeamType;

/**
 * Maps the properties of a {@link Team} to {@link TeamJson}
 * 
 * @author Andre Winkler
 */
public class TeamJsonMapper {

    public static TeamJson map(Team team, TeamJson teamJson) {
        teamJson.setId(team.getId());
        teamJson.setOpenligaid(team.getOpenligaid());
        teamJson.setLogo(team.getLogo());
        teamJson.setLongName(team.getLongName());
        teamJson.setName(team.getName());
        teamJson.setShortName(team.getShortName());
        teamJson.setXshortName(team.getXshortName());
        teamJson.setType(team.getTeamType().name());
        return teamJson;
    }

    public static List<TeamJson> map(List<Team> teams) {
        return teams.stream().map(TeamJsonMapper::map).toList();
    }
    
    private static TeamJson map(Team team) {
    	return map(team, new TeamJson());
    }

    public static Team reverse(TeamJson teamJson, Team team) {
        team.setOpenligaid(teamJson.getOpenligaid());
        team.setLogo(teamJson.getLogo());
        team.setLongName(teamJson.getLongName());
        team.setName(teamJson.getName());
        team.setShortName(teamJson.getShortName());
        team.setXshortName(teamJson.getXshortName());
        team.setTeamType(TeamType.valueOf(teamJson.getType()));
        return team;
    }

}
