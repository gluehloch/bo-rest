/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2000-2020 by Andre Winkler. All
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

import de.betoffice.web.json.GameJson;
import de.betoffice.web.json.IGameJson;
import de.winkler.betoffice.storage.Game;
import de.winkler.betoffice.storage.GameList;
import de.winkler.betoffice.storage.GameResult;
import de.winkler.betoffice.storage.Group;
import de.winkler.betoffice.storage.Location;
import de.winkler.betoffice.storage.Team;

/**
 * Test for {@link GameJsonMapper}.
 * 
 * @author Andre Winkler
 */
public class GameJsonMapperTest {

    @Test
    public void testGameJsonMapper() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Berlin"));

        Game game = new Game();
        game.setDateTime(now);
        game.setGroup(new Group());
        game.setGuestTeam(new Team("RWE"));
        game.setHomeTeam(new Team("S04"));
        game.setHalfTimeGoals(new GameResult(1, 0));
        // game.setIndex(1);
        game.setResult(1, 2);
        game.setPlayed(true);
        GameList gameList = new GameList();
        gameList.addGame(game);
        Location gelsenkirchen = new Location();
        gelsenkirchen.setCity("Gelsenkirchen");
        gelsenkirchen.setName("Parkstadion");
        game.setLocation(gelsenkirchen);

        GameJsonMapper gameJsonMapper = new GameJsonMapper();
        IGameJson gameJson = gameJsonMapper.map(game, new GameJson());

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
