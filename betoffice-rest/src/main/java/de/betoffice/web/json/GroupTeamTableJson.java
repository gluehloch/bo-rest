/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2016 by Andre Winkler. All rights
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

package de.betoffice.web.json;

import java.util.ArrayList;
import java.util.List;

/**
 * A group with all teams ordered by ranking.
 * 
 * @author Andre Winkler
 */
public class GroupTeamTableJson {

    private GroupTypeJson groupTypeJson;

    private List<TeamResultJson> teamResultJsons = new ArrayList<>();

    /**
     * @return the groupJson
     */
    public GroupTypeJson getGroupTypeJson() {
        return groupTypeJson;
    }

    /**
     * @param groupJson
     *            the groupJson to set
     */
    public void setGroupTypeJson(GroupTypeJson groupJson) {
        this.groupTypeJson = groupJson;
    }

    /**
     * Add {@link TeamResultJson}.
     * 
     * @param teamResultJson
     *            team result as JSON object
     */
    public void add(TeamResultJson teamResultJson) {
        teamResultJsons.add(teamResultJson);
    }

    /**
     * @return the teamResultJsons
     */
    public List<TeamResultJson> getTeamResultJsons() {
        return teamResultJsons;
    }

    /**
     * @param teamResultJsons
     *            the teamResultJsons to set
     */
    public void setTeamResultJsons(List<TeamResultJson> teamResultJsons) {
        this.teamResultJsons = teamResultJsons;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GroupTeamTableJson [groupJson=" + groupTypeJson
                + ", teamResultJsons=" + teamResultJsons + "]";
    }

}
