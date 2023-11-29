/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2013-2017 by Andre Winkler. All
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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * The game data as JSON.
 * 
 * @author Andre Winkler
 */
public class GameJson extends AbstractOpenligaid implements IGameJson {

    private int index;
    private Long roundId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JsonDateTimeFormat.DATETIME_PATTERN, timezone = JsonDateTimeFormat.TIMZONE)
    private ZonedDateTime dateTime;
    private TeamJson homeTeam;
    private TeamJson guestTeam;
    private GameResultJson halfTimeResult;
    private GameResultJson result;
    private GameResultJson overtimeResult;
    private GameResultJson penaltyResult;
    private boolean finished;
    private boolean ko;

    private List<GameTippJson> tipps = new ArrayList<>();

    @Override
	public int getIndex() {
        return index;
    }

    @Override
	public void setIndex(int index) {
        this.index = index;
    }
    
    @Override
	public Long getRoundId() {
        return roundId;
    }
    
    @Override
	public void setRoundId(Long roundId)  {
        this.roundId = roundId;
    }

    @Override
	public ZonedDateTime getDateTime() {
        return dateTime;
    }

    @Override
	public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
	public TeamJson getHomeTeam() {
        return homeTeam;
    }

    @Override
	public void setHomeTeam(TeamJson homeTeam) {
        this.homeTeam = homeTeam;
    }

    @Override
	public TeamJson getGuestTeam() {
        return guestTeam;
    }

    @Override
	public void setGuestTeam(TeamJson guestTeam) {
        this.guestTeam = guestTeam;
    }

    @Override
	public GameResultJson getHalfTimeResult() {
        return halfTimeResult;
    }

    @Override
	public void setHalfTimeResult(GameResultJson halfTimeResult) {
        this.halfTimeResult = halfTimeResult;
    }

    @Override
	public GameResultJson getResult() {
        return result;
    }

    @Override
	public void setResult(GameResultJson result) {
        this.result = result;
    }

    @Override
	public GameResultJson getOvertimeResult() {
        return overtimeResult;
    }

    @Override
	public void setOvertimeResult(GameResultJson overtimeResult) {
        this.overtimeResult = overtimeResult;
    }

    @Override
	public GameResultJson getPenaltyResult() {
        return penaltyResult;
    }

    @Override
	public void setPenaltyResult(GameResultJson penaltyResult) {
        this.penaltyResult = penaltyResult;
    }

    @Override
	public boolean isFinished() {
        return finished;
    }

    @Override
	public void setFinished(boolean finished) {
        this.finished = finished;
    }
    
    @Override
	public boolean isKo() {
        return ko;
    }
    
    @Override
	public void setKo(boolean ko) {
        this.ko = ko;
    }

    public List<GameTippJson> getTipps() {
        return tipps;
    }

    public void setTipps(List<GameTippJson> tipps) {
        this.tipps.clear();
        this.tipps.addAll(tipps);
    }

    public void addTipp(GameTippJson tipp) {
        this.tipps.add(tipp);
    }

    @Override
    public String toString() {
        return "GameJson [index=" + index + ", roundId=" + roundId
                + ", dateTime=" + dateTime + ", homeTeam=" + homeTeam
                + ", guestTeam=" + guestTeam + ", halfTimeResult="
                + halfTimeResult + ", result=" + result + ", overtimeResult="
                + overtimeResult + ", penaltyResult=" + penaltyResult
                + ", finished=" + finished + ", ko=" + ko + ", tipps=" + tipps
                + "]";
    }

}
