/*
 * ============================================================================
 * Project betoffice-jadmin Copyright (c) 2013-2014 by Andre Winkler. All rights
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

import java.io.Serializable;

/**
 * GameResult as JSON object.
 * 
 * @author Andre Winkler
 */
public class GameResultJson implements Serializable {

    private static final long serialVersionUID = -3317911005488437833L;

    private int homeGoals;
    private int guestGoals;

    /**
     * @return the homeGoals
     */
    public int getHomeGoals() {
        return homeGoals;
    }

    /**
     * @param homeGoals
     *            the homeGoals to set
     */
    public void setHomeGoals(int homeGoals) {
        this.homeGoals = homeGoals;
    }

    /**
     * @return the guestGoals
     */
    public int getGuestGoals() {
        return guestGoals;
    }

    /**
     * @param guestGoals
     *            the guestGoals to set
     */
    public void setGuestGoals(int guestGoals) {
        this.guestGoals = guestGoals;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GameResultJson [homeGoals=" + homeGoals + ", guestGoals="
                + guestGoals + "]";
    }
}
