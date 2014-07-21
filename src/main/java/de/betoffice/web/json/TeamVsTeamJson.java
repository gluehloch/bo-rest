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

/**
 * Team versus team info.
 * 
 * @author by Andre Winkler, $LastChangedBy$
 * @version $LastChangedRevision$ $LastChangedDate$
 */
public class TeamVsTeamJson {

	private String homeTeamName;
	private String guestTeamName;
	private String matchDate;
	private int homeTeamGoals;
	private int guestTeamGoals;

	/**
	 * @return the homeTeamName
	 */
	public String getHomeTeamName() {
		return homeTeamName;
	}

	/**
	 * @param homeTeamName
	 *            the homeTeamName to set
	 */
	public void setHomeTeamName(String homeTeamName) {
		this.homeTeamName = homeTeamName;
	}

	/**
	 * @return the guestTeamName
	 */
	public String getGuestTeamName() {
		return guestTeamName;
	}

	/**
	 * @param guestTeamName
	 *            the guestTeamName to set
	 */
	public void setGuestTeamName(String guestTeamName) {
		this.guestTeamName = guestTeamName;
	}

	/**
	 * @return the matchDate
	 */
	public String getMatchDate() {
		return matchDate;
	}

	/**
	 * @param matchDate
	 *            the matchDate to set
	 */
	public void setMatchDate(String matchDate) {
		this.matchDate = matchDate;
	}

	/**
	 * @return the homeTeamGoals
	 */
	public int getHomeTeamGoals() {
		return homeTeamGoals;
	}

	/**
	 * @param homeTeamGoals
	 *            the homeTeamGoals to set
	 */
	public void setHomeTeamGoals(int homeTeamGoals) {
		this.homeTeamGoals = homeTeamGoals;
	}

	/**
	 * @return the guestTeamGoals
	 */
	public int getGuestTeamGoals() {
		return guestTeamGoals;
	}

	/**
	 * @param guestTeamGoals
	 *            the guestTeamGoals to set
	 */
	public void setGuestTeamGoals(int guestTeamGoals) {
		this.guestTeamGoals = guestTeamGoals;
	}

	@Override
	public String toString() {
		return "TeamVsTeamJson [homeTeamName=" + homeTeamName
				+ ", guestTeamName=" + guestTeamName + ", matchDate="
				+ matchDate + ", homeTeamGoals=" + homeTeamGoals
				+ ", guestTeamGoals=" + guestTeamGoals + "]";
	}

}
