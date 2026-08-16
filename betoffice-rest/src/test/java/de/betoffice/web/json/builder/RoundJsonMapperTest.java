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

import de.betoffice.storage.group.entity.GroupTypeEntity;
import de.betoffice.storage.season.RoundDto;
import de.betoffice.storage.season.SeasonType;
import de.betoffice.storage.season.entity.GameListEntity;
import de.betoffice.storage.season.entity.GroupEntity;
import de.betoffice.storage.season.entity.RoundDtoMapper;
import de.betoffice.storage.season.entity.SeasonEntity;
import de.betoffice.storage.season.entity.SeasonReference;

/**
 * Test for {@link RoundDtoMapper}.
 * 
 * @author Andre Winkler
 */
class RoundJsonMapperTest {

    @Test
    void testRoundJsonMapper() {
        GroupTypeEntity groupType = new GroupTypeEntity();
        groupType.setName("1. Bundesliga");
        SeasonEntity season = new SeasonEntity();
        season.setMode(SeasonType.LEAGUE);
        season.setReference(SeasonReference.of("1987/1988", "Bundesliga"));
        GroupEntity group = new GroupEntity();
        group.setGroupType(groupType);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Berlin"));
        GameListEntity round = new GameListEntity();
        round.setDateTime(now);
        round.setGroup(group);
        round.setIndex(0);
        round.setOpenligaid(4711L);
        round.setSeason(season);

        RoundDtoMapper mapper = new RoundDtoMapper();
        RoundDto roundJson = mapper.map(round, new RoundDto());

        assertThat(roundJson.getDateTime()).isEqualTo(now);
        assertThat(roundJson.getIndex()).isEqualTo(1);
        assertThat(roundJson.getSeasonName()).isEqualTo("Bundesliga");
        assertThat(roundJson.getSeasonYear()).isEqualTo("1987/1988");
    }

}
