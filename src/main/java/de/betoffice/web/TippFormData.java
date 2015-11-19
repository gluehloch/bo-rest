/*
 * ============================================================================
 * Project betoffice-jweb
 * Copyright (c) 2000-2015 by Andre Winkler. All rights reserved.
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
package de.betoffice.web;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the data of a tipp form.
 * 
 * @author by Andre Winkler
 */
public class TippFormData {

    /** Users nick name. */
    private String nickname;

    /** Users password */
    private String password;

    /** Associated championship of the tipp form. */
    private long championship;

    /** Number of matches to tipp. */
    private int matches;

    /** Round number. */
    private int roundNr;

    private final List<GameFormData> gameFormDatas = new ArrayList<GameFormData>();

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
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the championship
     */
    public long getChampionship() {
        return championship;
    }

    /**
     * @param championship
     *            the championship to set
     */
    public void setChampionship(long championship) {
        this.championship = championship;
    }

    /**
     * @return the matches
     */
    public int getMatches() {
        return matches;
    }

    /**
     * @param matches
     *            the matches to set
     */
    public void setMatches(int matches) {
        this.matches = matches;
    }

    /**
     * @return the roundNr
     */
    public int getRoundNr() {
        return roundNr;
    }

    /**
     * @param roundNr
     *            the roundNr to set
     */
    public void setRoundNr(int roundNr) {
        this.roundNr = roundNr;
    }

    /**
     * 
     * @param gameFormData
     */
    public void add(final GameFormData gameFormData) {
        gameFormDatas.add(gameFormData);
    }

    public List<GameFormData> getGameFormDatas() {
        return gameFormDatas;
    }

    public int[] getHomeGoals() {
        int[] homeGoals = new int[gameFormDatas.size()];
        for (int index = 0; index < homeGoals.length; index++) {
            homeGoals[index] = gameFormDatas.get(index).getHomeGoals();
        }
        return homeGoals;
    }

    public int[] getGuestGoals() {
        int[] guestGoals = new int[gameFormDatas.size()];
        for (int index = 0; index < guestGoals.length; index++) {
            guestGoals[index] = gameFormDatas.get(index).getGuestGoals();
        }
        return guestGoals;
    }

    public String[] getHomeTeams() {
        String[] homeTeams = new String[gameFormDatas.size()];
        for (int index = 0; index < homeTeams.length; index++) {
            homeTeams[index] = gameFormDatas.get(index).getHomeTeam();
        }
        return homeTeams;
    }

    public String[] getGuestTeams() {
        String[] guestTeams = new String[gameFormDatas.size()];
        for (int index = 0; index < guestTeams.length; index++) {
            guestTeams[index] = gameFormDatas.get(index).getGuestTeam();
        }
        return guestTeams;
    }

    public static class GameFormData {

        private long matchId;
        private int matchNr;
        private String homeTeam;
        private long homeTeamId;
        private String guestTeam;
        private long guestTeamId;
        private int homeGoals;
        private int guestGoals;

        /**
         * @return the matchId
         */
        public long getMatchId() {
            return matchId;
        }

        /**
         * @param matchId
         *            the matchId to set
         */
        public void setMatchId(long matchId) {
            this.matchId = matchId;
        }

        /**
         * @return the matchNr
         */
        public int getMatchNr() {
            return matchNr;
        }

        /**
         * @param matchNr
         *            the matchNr to set
         */
        public void setMatchNr(int matchNr) {
            this.matchNr = matchNr;
        }

        /**
         * @return the homeTeam
         */
        public String getHomeTeam() {
            return homeTeam;
        }

        /**
         * @param homeTeam
         *            the homeTeam to set
         */
        public void setHomeTeam(String homeTeam) {
            this.homeTeam = homeTeam;
        }

        /**
         * @return the homeTeamId
         */
        public long getHomeTeamId() {
            return homeTeamId;
        }

        /**
         * @param homeTeamId
         *            the homeTeamId to set
         */
        public void setHomeTeamId(long homeTeamId) {
            this.homeTeamId = homeTeamId;
        }

        /**
         * @return the guestTeam
         */
        public String getGuestTeam() {
            return guestTeam;
        }

        /**
         * @param guestTeam
         *            the guestTeam to set
         */
        public void setGuestTeam(String guestTeam) {
            this.guestTeam = guestTeam;
        }

        /**
         * @return the guestTeamId
         */
        public long getGuestTeamId() {
            return guestTeamId;
        }

        /**
         * @param guestTeamId
         *            the guestTeamId to set
         */
        public void setGuestTeamId(long guestTeamId) {
            this.guestTeamId = guestTeamId;
        }

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

    }

}
