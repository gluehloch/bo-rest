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

/**
 * Round and table data.
 *
 * @author Andre Winkler
 */
public class RoundAndTableJson {

    private RoundJson roundJson;
    private GroupTeamTableJson groupTeamTableJsons;

    /**
     * @return the roundJson
     */
    public RoundJson getRoundJson() {
        return roundJson;
    }

    /**
     * @param roundJson
     *            the roundJson to set
     */
    public void setRoundJson(RoundJson roundJson) {
        this.roundJson = roundJson;
    }

    /**
     * @return the groupTeamTableJson
     */
    public GroupTeamTableJson getGroupTeamTableJsons() {
        return groupTeamTableJsons;
    }

    /**
     * @param groupTeamTableJson
     *            the groupTeamTableJson to set
     */
    public void setGroupTeamTableJson(GroupTeamTableJson groupTeamTableJson) {
        groupTeamTableJsons = groupTeamTableJson;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "RoundAndTableJson [roundJson=" + roundJson
                + ", groupTeamTableJsons=" + groupTeamTableJsons + "]";
    }

}
