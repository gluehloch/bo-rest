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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import de.winkler.betoffice.storage.enums.SeasonType;

/**
 * JSON data for a round.
 * 
 * @author Andre Winkler
 */
public class RoundJson extends AbstractIdentifier {

    private Long seasonId;
    private String seasonName;
    private String seasonYear;
    private String seasonType;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JsonDateTimeFormat.DATETIME_PATTERN, timezone = JsonDateTimeFormat.TIMZONE)
    private ZonedDateTime dateTime;

    /** This index has a range from 1..N (number of rounds) */
    private int index;
    private boolean lastRound;
    private boolean tippable;

    private List<GameJson> games = new ArrayList<>();

    public final Long getSeasonId() {
        return seasonId;
    }

    public final void setSeasonId(Long seasonId) {
        this.seasonId = seasonId;
    }

    public final String getSeasonName() {
        return seasonName;
    }

    public final void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    public final String getSeasonYear() {
        return seasonYear;
    }

    public final void setSeasonYear(String seasonYear) {
        this.seasonYear = seasonYear;
    }

    public final String getSeasonType() {
        return seasonType;
    }

    public final void setSeasonType(SeasonType seasonType) {
        this.seasonType = seasonType.name();
    }

    public final ZonedDateTime getDateTime() {
        return dateTime;
    }

    public final void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public final int getIndex() {
        return index;
    }

    public final void setIndex(int index) {
        this.index = index;
    }

    public final boolean isLastRound() {
        return lastRound;
    }

    public final void setLastRound(boolean lastRound) {
        this.lastRound = lastRound;
    }

    public final boolean isTippable() {
        return tippable;
    }

    public final void setTippable(boolean tippable) {
        this.tippable = tippable;
    }

    public final List<GameJson> getGames() {
        return games;
    }

    public final void setGames(List<GameJson> _games) {
        games = _games;
    }

    @Override
    public String toString() {
        return "RoundJson [games=" + games + "]";
    }

}
