/*
 * ============================================================================
 * Project betoffice-jweb Copyright (c) 2015-2023 by Andre Winkler. All rights
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

package de.betoffice.web.json.builder;

import java.util.List;

import de.betoffice.storage.season.GameResultDto;
import de.betoffice.storage.season.GoalDto;
import de.betoffice.storage.season.entity.GoalEntity;

public class GoalJsonMapper {

    public static GoalDto map(GoalEntity goal, GoalDto json) {
        json.setGameResult(GameResultJsonMapper.map(goal.getResult(), new GameResultDto()));
        json.setPlayerName(goal.getPlayer().getName());
        json.setMinute(goal.getMinute());
        json.setOpenligaid(goal.getOpenligaid());
        json.setGoalType(goal.getGoalType());
        return json;
    }

    public static List<GoalDto> map(List<GoalEntity> goals) {
        return goals.stream().map(GoalJsonMapper::map).toList();
    }

    private static GoalDto map(GoalEntity goal) {
        return map(goal, new GoalDto());
    }

}
