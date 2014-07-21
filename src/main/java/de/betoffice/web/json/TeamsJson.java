/*
 * $Id$
 * ============================================================================
 * Project betoffice-jweb
 * Copyright (c) 2000-2014 by Andre Winkler. All rights reserved.
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

package de.betoffice.web.json;

import java.util.ArrayList;
import java.util.List;

/**
 * All selectable teams.
 * 
 * @author by Andre Winkler, $LastChangedBy$
 * @version $LastChangedRevision$ $LastChangedDate$
 */
public class TeamsJson {

	private List<TeamJson> teamJsons = new ArrayList<>();

	public List<TeamJson> getTeamJsons() {
		return teamJsons;
	}

	public void addTeamJson(TeamJson teamJson) {
		teamJsons.add(teamJson);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("TeamsJson [");
		sb.append("]");
		for (TeamJson team : teamJsons) {
			sb.append("(").append(team).append(")");
		}
		return sb.toString();
	}

}
