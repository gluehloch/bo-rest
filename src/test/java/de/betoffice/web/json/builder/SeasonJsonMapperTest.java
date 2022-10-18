/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2017-2020 by Andre Winkler. All rights
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

import de.betoffice.web.json.SeasonJson;
import de.winkler.betoffice.storage.Season;
import de.winkler.betoffice.storage.enums.SeasonType;
import de.winkler.betoffice.storage.enums.TeamType;

/**
 * Test for {@link SeasonJsonMapper}.
 * 
 * @author Andre Winkler
 */
public class SeasonJsonMapperTest {

    @Test
    public void testSeasonJsonMapper() {
        Season season = new Season();
        season.setMode(SeasonType.LEAGUE);
        season.setName("Bundesliga 2017/2018");
        season.setTeamType(TeamType.DFB);
        season.setYear("2017/2018");

        SeasonJsonMapper seasonJsonMapper = new SeasonJsonMapper();
        SeasonJson seasonJson = seasonJsonMapper.map(season, new SeasonJson());

        assertThat(seasonJson.getName()).isEqualTo("Bundesliga 2017/2018");
        assertThat(seasonJson.getSeasonType()).isEqualTo(SeasonType.LEAGUE.toString());
        assertThat(seasonJson.getTeamType()).isEqualTo(TeamType.DFB.toString());
        assertThat(seasonJson.getYear()).isEqualTo("2017/2018");
    }

}
