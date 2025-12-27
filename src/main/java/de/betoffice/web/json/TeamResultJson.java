/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2016-2020 by Andre Winkler.
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

package de.betoffice.web.json;

import java.io.Serializable;

import de.betoffice.storage.team.TeamResult;

/**
 * A {@link TeamResult} as JSON object.
 * 
 * @author Andre Winkler
 */
public class TeamResultJson implements Serializable {

    private static final long serialVersionUID = 398170755718207864L;

    private TeamJson team;
    private int posGoals;
    private int negGoals;
    private int win;
    private int lost;
    private int remis;
    private int tablePosition;

    public TeamJson getTeam() {
        return team;
    }
    
    public void setTeam(TeamJson team) {
        this.team = team;
    }
    
    public int getPosGoals() {
        return posGoals;
    }

    public void setPosGoals(int posGoals) {
        this.posGoals = posGoals;
    }

    public int getNegGoals() {
        return negGoals;
    }

    public void setNegGoals(int negGoals) {
        this.negGoals = negGoals;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getLost() {
        return lost;
    }

    public void setLost(int lost) {
        this.lost = lost;
    }

    public int getRemis() {
        return remis;
    }

    public void setRemis(int remis) {
        this.remis = remis;
    }

    public int getTablePosition() {
        return tablePosition;
    }

    public void setTablePosition(int tablePosition) {
        this.tablePosition = tablePosition;
    }

    @Override
    public String toString() {
        return "TeamResultJson [team=" + team + ", posGoals=" + posGoals + ", negGoals=" + negGoals + ", win=" + win
                + ", lost=" + lost + ", remis=" + remis + ", tablePosition=" + tablePosition + "]";
    }

}
