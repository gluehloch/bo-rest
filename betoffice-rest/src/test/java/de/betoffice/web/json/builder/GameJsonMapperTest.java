/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2000-2024 by Andre Winkler. All
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

package de.betoffice.web.json.builder;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import de.betoffice.storage.group.entity.GroupTypeEntity;
import de.betoffice.storage.season.GameDto;
import de.betoffice.storage.season.entity.GameEntity;
import de.betoffice.storage.season.entity.GameListEntity;
import de.betoffice.storage.season.entity.GameResult;
import de.betoffice.storage.season.entity.GroupEntity;
import de.betoffice.storage.season.entity.LocationEntity;
import de.betoffice.storage.team.entity.TeamEntity;
import de.betoffice.web.json.IGameJson;

/**
 * Test for {@link GameJsonMapper}.
 * 
 * @author Andre Winkler
 */
public class GameJsonMapperTest {

    @Test
    public void testGameJsonMapper() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Berlin"));

        GroupTypeEntity groupType = new GroupTypeEntity();
        groupType.setName("1. Bundesliga");
        GroupEntity group = new GroupEntity();
        group.setGroupType(groupType);
        GameEntity game = new GameEntity();
        game.setDateTime(now);
        game.setGroup(group);
        game.setGuestTeam(new TeamEntity("RWE"));
        game.setHomeTeam(new TeamEntity("S04"));
        game.setHalfTimeGoals(new GameResult(1, 0));
        // game.setIndex(1);
        game.setResult(1, 2);
        game.setPlayed(true);
        GameListEntity gameList = new GameListEntity();
        gameList.addGame(game);
        LocationEntity gelsenkirchen = new LocationEntity();
        gelsenkirchen.setCity("Gelsenkirchen");
        gelsenkirchen.setName("Parkstadion");
        game.setLocation(gelsenkirchen);

        IGameJson gameJson = GameJsonMapper.map(game, new GameDto());

        assertThat(gameJson.isFinished()).isTrue();
        assertThat(gameJson.getDateTime()).isEqualTo(now);
        assertThat(gameJson.getGuestTeam().getName()).isEqualTo("RWE");
        assertThat(gameJson.getHomeTeam().getName()).isEqualTo("S04");
        assertThat(gameJson.getHalfTimeResult().getHomeGoals()).isEqualTo(1);
        assertThat(gameJson.getHalfTimeResult().getGuestGoals()).isEqualTo(0);
        assertThat(gameJson.getResult().getHomeGoals()).isEqualTo(1);
        assertThat(gameJson.getResult().getGuestGoals()).isEqualTo(2);
    }

}
