/*
 * ============================================================================
 * Project betoffice-jadmin Copyright (c) 2013 by Andre Winkler. All rights
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
import java.util.ArrayList;
import java.util.List;

/**
 * Season for JSON serialization.
 *
 * @author Andre Winkler
 */
public class SeasonJson extends AbstractIdentifier implements Serializable {

    private static final long serialVersionUID = 1243022582656671229L;

    private String name;
    private String year;
    private String seasonType;
    private String teamType;

    private String openligaLeagueShortcut;
    private String openligaLeagueSeason;

    private Long currentRoundId;
    private List<RoundJson> rounds = new ArrayList<>();

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the year
     */
    public String getYear() {
        return year;
    }

    /**
     * @param year
     *            the year to set
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Add a round
     *
     * @param round
     *            the round to add
     */
    public void addRound(RoundJson round) {
        rounds.add(round);
    }

    /**
     * A list of rounds.
     *
     * @return the rounds
     */
    public List<RoundJson> getRounds() {
        return rounds;
    }

    /**
     * @return the seasonType
     */
    public String getSeasonType() {
        return seasonType;
    }

    /**
     * @param seasonType
     *            the seasonType to set
     */
    public void setSeasonType(String seasonType) {
        this.seasonType = seasonType;
    }

    /**
     * @return the teamType
     */
    public String getTeamType() {
        return teamType;
    }

    /**
     * @param teamType
     *            the teamType to set
     */
    public void setTeamType(String teamType) {
        this.teamType = teamType;
    }

    public String getOpenligaLeagueShortcut() {
        return openligaLeagueShortcut;
    }

    public void setOpenligaLeagueShortcut(String openligaLeagueShortcut) {
        this.openligaLeagueShortcut = openligaLeagueShortcut;
    }

    public String getOpenligaLeagueSeason() {
        return openligaLeagueSeason;
    }

    public void setOpenligaLeagueSeason(String openligaLeagueSeason) {
        this.openligaLeagueSeason = openligaLeagueSeason;
    }

    public Long getCurrentRoundId() {
        return currentRoundId;
    }

    public void setCurrentRoundId(Long currentRoundId) {
        this.currentRoundId = currentRoundId;
    }

    @Override
    public String toString() {
        return "SeasonJson [name=" + name + ", year=" + year + ", seasonType="
                + seasonType + ", teamType=" + teamType
                + ", openligaLeagueShortcut=" + openligaLeagueShortcut
                + ", openligaLeagueSeason=" + openligaLeagueSeason
                + ", currentRoundId=" + currentRoundId + ", rounds=" + rounds
                + "]";
    }

}
