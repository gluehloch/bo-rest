/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2013-2015 by Andre Winkler. All
 * rights reserved.
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
 * Holds the submit tipp for a game for one user.
 * 
 * @author Andre Winkler
 */
public class SubmitTippGameJson implements Serializable {

    private static final long serialVersionUID = -1535458861691048504L;

    private long gameId;
    private GameResultJson tippResult;

    /**
     * @return the gameId
     */
    public long getGameId() {
        return gameId;
    }

    /**
     * @param gameId
     *            the gameId to set
     */
    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    /**
     * @return the tippResult
     */
    public GameResultJson getTippResult() {
        return tippResult;
    }

    /**
     * @param tippResult
     *            the tippResult to set
     */
    public void setTippResult(GameResultJson tippResult) {
        this.tippResult = tippResult;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TippGameJson [gameId=" + gameId + ", tippResult=" + tippResult
                + "]";
    }

}
