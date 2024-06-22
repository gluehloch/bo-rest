/*
 * ============================================================================
 * Project betoffice-jweb
 * Copyright (c) 2000-2017 by Andre Winkler. All rights reserved.
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

import java.util.List;

import de.winkler.betoffice.storage.Game;

/**
 * Mapping
 *
 * @author Andre Winkler
 */
public class HistoryTeamVsTeamJsonMapper {

    public static HistoryTeamVsTeamJson map(List<Game> games) {
        HistoryTeamVsTeamJson history = new HistoryTeamVsTeamJson();
        for (Game game : games) {
            TeamVsTeamJson json = new TeamVsTeamJson();
            json.setHomeTeamName(game.getHomeTeam().getName());
            json.setGuestTeamName(game.getGuestTeam().getName());
            json.setHomeTeamGoals(game.getResult().getHomeGoals());
            json.setGuestTeamGoals(game.getResult().getGuestGoals());
            json.setMatchDate(game.getDateTime());
            history.addTeamVsTeamJson(json);
        }
        return history;
    }

}
