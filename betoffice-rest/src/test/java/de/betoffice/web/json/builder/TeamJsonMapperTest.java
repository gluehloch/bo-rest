/*
 * ============================================================================
 * Project betoffice-jweb-misc 
 * Copyright (c) 2000-2020 by Andre Winkler. All rights reserved.
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

package de.betoffice.web.json.builder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.betoffice.storage.season.entity.LocationEntity;
import de.betoffice.storage.team.TeamDto;
import de.betoffice.storage.team.TeamType;
import de.betoffice.storage.team.entity.TeamEntity;
import de.betoffice.storage.team.entity.TeamDtoMapper;

/**
 * Test for {@link TeamDtoMapper}.
 * 
 * @author Andre Winkler
 */
public class TeamJsonMapperTest {

    @Test
    public void testTeamJsonMapper() {
        TeamEntity team = createTeam();

        TeamDtoMapper mapper = new TeamDtoMapper();
        TeamDto teamJson = new TeamDto();

        teamJson = mapper.map(team, teamJson);

        assertThat(teamJson.getOpenligaid()).isEqualTo(4711L);
        assertThat(teamJson.getLogo()).isEqualTo("logo.gif");
        assertThat(teamJson.getName()).isEqualTo("RWE");
        assertThat(teamJson.getLongName()).isEqualTo("Rot-Weiss-Essen");
        assertThat(teamJson.getType()).isEqualTo(TeamType.DFB.toString());
    }

    @Test
    public void testTeamJsonMapperForLists() {
        TeamEntity t1 = createTeam();
        TeamEntity t2 = createTeam();
        TeamEntity t3 = createTeam();
        List<TeamEntity> teams = new ArrayList<>();
        teams.add(t1);
        teams.add(t2);
        teams.add(t3);

        TeamDtoMapper mapper = new TeamDtoMapper();
        List<TeamDto> teamJsons = new ArrayList<TeamDto>();

        teamJsons = mapper.map(teams);

        assertThat(teamJsons.get(0).getId()).isNull();
        assertThat(teamJsons.get(0).getOpenligaid()).isEqualTo(t1.getOpenligaid());
        assertThat(teamJsons.get(0).getLogo()).isEqualTo(t1.getLogo());
        assertThat(teamJsons.get(0).getName()).isEqualTo(t1.getName());
        assertThat(teamJsons.get(0).getLongName()).isEqualTo(t1.getLongName());
        assertThat(teamJsons.get(0).getType()).isEqualTo(t1.getTeamType().toString());

        assertThat(teamJsons.get(1).getId()).isNull();
        assertThat(teamJsons.get(1).getOpenligaid()).isEqualTo(t1.getOpenligaid());
        assertThat(teamJsons.get(1).getLogo()).isEqualTo(t1.getLogo());
        assertThat(teamJsons.get(1).getName()).isEqualTo(t1.getName());
        assertThat(teamJsons.get(1).getLongName()).isEqualTo(t1.getLongName());
        assertThat(teamJsons.get(1).getType()).isEqualTo(t1.getTeamType().toString());

        assertThat(teamJsons.get(2).getId()).isNull();
        assertThat(teamJsons.get(2).getOpenligaid()).isEqualTo(t2.getOpenligaid());
        assertThat(teamJsons.get(2).getLogo()).isEqualTo(t2.getLogo());
        assertThat(teamJsons.get(2).getName()).isEqualTo(t2.getName());
        assertThat(teamJsons.get(2).getLongName()).isEqualTo(t2.getLongName());
        assertThat(teamJsons.get(2).getType()).isEqualTo(t2.getTeamType().toString());
    }

    private TeamEntity createTeam() {
        TeamEntity team = new TeamEntity();
        team.setOpenligaid(1L);
        team.setName("RWE");
        team.setLongName("Rot-Weiss-Essen");
        team.setOpenligaid(4711L);
        team.setTeamType(TeamType.DFB);
        team.setLogo("logo.gif");
        LocationEntity location = new LocationEntity();
        location.setCity("Essen");
        location.setId(4712L);
        location.setName("Essen");
        team.setLocation(location);
        return team;
    }

}
