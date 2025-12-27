/*
 * ============================================================================
 * Project betoffice-jweb Copyright (c) 2016 by Andre Winkler. All rights
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
 * Holds user ranking informations
 * 
 * @author Andre Winkler
 */
public class UserJson extends AbstractIdentifier {

    private String nickname;
    private int win;
    private int toto;
    private int ticket;
    private long points;
    private int position;

    /**
     * @return the nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname
     *            the nickname to set
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return the win
     */
    public int getWin() {
        return win;
    }

    /**
     * @param win
     *            the win to set
     */
    public void setWin(int win) {
        this.win = win;
    }

    /**
     * @return the toto
     */
    public int getToto() {
        return toto;
    }

    /**
     * @param toto
     *            the toto to set
     */
    public void setToto(int toto) {
        this.toto = toto;
    }

    /**
     * @return the ticket
     */
    public int getTicket() {
        return ticket;
    }

    /**
     * @param ticket
     *            the ticket to set
     */
    public void setTicket(int ticket) {
        this.ticket = ticket;
    }

    /**
     * @return the points
     */
    public long getPoints() {
        return points;
    }

    /**
     * @param points
     *            the points to set
     */
    public void setPoints(long points) {
        this.points = points;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position
     *            the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UserJson [nickname=" + nickname + ", win=" + win + ", toto="
                + toto + ", ticket=" + ticket + ", points=" + points
                + ", position=" + position + "]";
    }

}
