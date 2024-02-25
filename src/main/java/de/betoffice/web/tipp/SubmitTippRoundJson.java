/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2013-2024 by Andre Winkler. All
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

package de.betoffice.web.tipp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the submit tipp for a round of one user.
 * 
 * @author Andre Winkler
 */
public class SubmitTippRoundJson implements Serializable {

    private static final long serialVersionUID = -1131645240778072140L;

    private String nickname;
    private long roundId;

    private List<SubmitTippGameJson> submitTippGames = new ArrayList<>();

    /**
     * @return the nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname the nickname to set
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return the roundId
     */
    public long getRoundId() {
        return roundId;
    }

    /**
     * @param roundId the roundId to set
     */
    public void setRoundId(long roundId) {
        this.roundId = roundId;
    }

    /**
     * @return the submitted tippGames
     */
    public List<SubmitTippGameJson> getSubmitTippGames() {
        return submitTippGames;
    }

    /**
     * @param submitTippGames the tippGameJsons to set
     */
    public void setSubmitTippGames(List<SubmitTippGameJson> submitTippGames) {
        this.submitTippGames = submitTippGames;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TippRoundJson [nickname=" + nickname + ", roundId=" + roundId
                + ", submitTippGames=" + submitTippGames + "]";
    }

}
