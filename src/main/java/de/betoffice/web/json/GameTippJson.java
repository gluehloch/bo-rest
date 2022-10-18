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

/**
 * Tipp for a game and an extension of {@link GameJson}.
 * 
 * @author Andre Winkler
 */
public class GameTippJson {

    private String nickname;
    private GameResultJson tipp;
    private long points;

    public GameResultJson getTipp() {
        return tipp;
    }

    public void setTipp(GameResultJson tipp) {
        this.tipp = tipp;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "GameTippJson [nickname=" + nickname + ", tipp=" + tipp
                + ", points=" + points + "]";
    }

}
