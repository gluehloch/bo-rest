/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2017-2022 by Andre Winkler. All rights
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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.betoffice.storage.season.SeasonDto;
import de.betoffice.storage.season.SeasonType;
import de.betoffice.storage.season.entity.SeasonEntity;
import de.betoffice.storage.season.entity.SeasonDtoMapper;
import de.betoffice.storage.season.entity.SeasonReference;
import de.betoffice.storage.team.TeamType;

/**
 * Test for {@link SeasonDtoMapper}.
 * 
 * @author Andre Winkler
 */
class SeasonJsonMapperTest {

    @Test
    void testSeasonJsonMapper() {
        SeasonEntity season = new SeasonEntity();
        season.setMode(SeasonType.LEAGUE);
        season.setReference(SeasonReference.of("2017/2018", "Bundesliga 2017/2018"));
        season.setTeamType(TeamType.DFB);

        SeasonDtoMapper seasonJsonMapper = new SeasonDtoMapper();
        SeasonDto seasonJson = seasonJsonMapper.map(season, new SeasonDto());

        assertThat(seasonJson.getName()).isEqualTo("Bundesliga 2017/2018");
        assertThat(seasonJson.getSeasonType()).isEqualTo(SeasonType.LEAGUE.toString());
        assertThat(seasonJson.getTeamType()).isEqualTo(TeamType.DFB.toString());
        assertThat(seasonJson.getYear()).isEqualTo("2017/2018");
    }

}
