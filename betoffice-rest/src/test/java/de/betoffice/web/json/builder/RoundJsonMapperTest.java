/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2013-2022 by Andre Winkler. All rights
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

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import de.betoffice.storage.group.entity.GroupType;
import de.betoffice.storage.season.SeasonType;
import de.betoffice.storage.season.entity.GameList;
import de.betoffice.storage.season.entity.Group;
import de.betoffice.storage.season.entity.Season;
import de.betoffice.storage.season.entity.SeasonReference;
import de.betoffice.web.json.RoundJson;

/**
 * Test for {@link RoundJsonMapper}.
 * 
 * @author Andre Winkler
 */
class RoundJsonMapperTest {

    @Test
    void testRoundJsonMapper() {
        GroupType groupType = new GroupType();
        groupType.setName("1. Bundesliga");
        Season season = new Season();
        season.setMode(SeasonType.LEAGUE);
        season.setReference(SeasonReference.of("1987/1988", "Bundesliga"));
        Group group = new Group();
        group.setGroupType(groupType);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Berlin"));
        GameList round = new GameList();
        round.setDateTime(now);
        round.setGroup(group);
        round.setIndex(0);
        round.setOpenligaid(4711L);
        round.setSeason(season);

        RoundJsonMapper mapper = new RoundJsonMapper();
        RoundJson roundJson = mapper.map(round, new RoundJson());

        assertThat(roundJson.getDateTime()).isEqualTo(now);
        assertThat(roundJson.getIndex()).isEqualTo(1);
        assertThat(roundJson.getSeasonName()).isEqualTo("Bundesliga");
        assertThat(roundJson.getSeasonYear()).isEqualTo("1987/1988");
    }

}
