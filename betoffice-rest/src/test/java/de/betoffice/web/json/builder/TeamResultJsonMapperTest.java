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

package de.betoffice.web.json.builder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.betoffice.web.json.TeamResultJson;
import de.winkler.betoffice.storage.GroupType;
import de.winkler.betoffice.storage.Season;
import de.winkler.betoffice.storage.Team;
import de.winkler.betoffice.storage.TeamResult;

/**
 * Mapping test for {@link TeamResult} to {@link TeamResultJson}.
 * 
 * @author Andre Winkler
 */
public class TeamResultJsonMapperTest {

    @Test
    public void testTeamResultJsonMapper() {
        Season season = new Season();
        GroupType groupType = new GroupType();
        Team team = new Team("RWE");

        TeamResult teamResult = new TeamResult(season, groupType, team);
        teamResult.setLost(1);
        teamResult.setNegGoals(2);
        teamResult.setPosGoals(3);
        teamResult.setRemis(4);
        teamResult.setTabPos(5);
        teamResult.setWin(6);

        TeamResultJsonMapper teamResultJsonMapper = new TeamResultJsonMapper();
        TeamResultJson teamResultJson = teamResultJsonMapper.map(teamResult, new TeamResultJson());

        assertThat(teamResultJson.getLost()).isEqualTo(1);
        assertThat(teamResultJson.getNegGoals()).isEqualTo(2);
        assertThat(teamResultJson.getPosGoals()).isEqualTo(3);
        assertThat(teamResultJson.getRemis()).isEqualTo(4);
        assertThat(teamResultJson.getTablePosition()).isEqualTo(5);
        assertThat(teamResultJson.getWin()).isEqualTo(6);
        assertThat(teamResultJson.getTeam().getName()).isEqualTo("RWE");
    }

}
