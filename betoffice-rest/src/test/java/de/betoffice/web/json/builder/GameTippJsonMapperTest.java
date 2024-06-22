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

import org.junit.jupiter.api.Test;

import de.betoffice.web.json.GameTippJson;
import de.winkler.betoffice.storage.Game;
import de.winkler.betoffice.storage.GameResult;
import de.winkler.betoffice.storage.GameTipp;
import de.winkler.betoffice.storage.Nickname;
import de.winkler.betoffice.storage.Team;
import de.winkler.betoffice.storage.User;
import de.winkler.betoffice.storage.enums.TippStatusType;

/**
 * Test for class {@link GameTippJsonMapper}.
 *
 * @author Andre Winkler
 */
class GameTippJsonMapperTest {

    @Test
    void testGameTippJsonMapperFullPoints() {
        User user = new User();
        user.setNickname(Nickname.of("Frosch"));

        Game game = new Game();
        game.setHomeTeam(new Team());
        game.setGuestTeam(new Team());
        game.setResult(new GameResult(2, 1));
        game.setPlayed(true);

        GameTipp tipp = new GameTipp();
        tipp.setToken("Token");
        tipp.setUser(user);
        tipp.setGame(game);
        tipp.setTipp(GameResult.of(2, 1), TippStatusType.USER);

        GameTippJsonMapper gameTippJsonMapper = new GameTippJsonMapper();
        GameTippJson gameTippJson = gameTippJsonMapper.map(tipp, new GameTippJson());

        assertThat(gameTippJson.getNickname()).isEqualTo("Frosch");
        assertThat(gameTippJson.getTipp().getHomeGoals()).isEqualTo(2);
        assertThat(gameTippJson.getTipp().getGuestGoals()).isEqualTo(1);
        assertThat(tipp.getPoints()).isEqualTo(13L);
        assertThat(gameTippJson.getPoints()).isEqualTo(13L);
    }

    @Test
    void testGameTippJsonMapperWinPoints() {
        User user = new User();
        user.setNickname(Nickname.of("Frosch"));

        Game game = new Game();
        game.setHomeTeam(new Team());
        game.setGuestTeam(new Team());
        game.setResult(new GameResult(2, 1));
        game.setPlayed(true);

        GameTipp tipp = new GameTipp();
        tipp.setToken("Token");
        tipp.setUser(user);
        tipp.setGame(game);
        tipp.setTipp(GameResult.of(2, 0), TippStatusType.USER);

        GameTippJsonMapper gameTippJsonMapper = new GameTippJsonMapper();
        GameTippJson gameTippJson = gameTippJsonMapper.map(tipp, new GameTippJson());

        assertThat(gameTippJson.getNickname()).isEqualTo("Frosch");
        assertThat(gameTippJson.getTipp().getHomeGoals()).isEqualTo(2);
        assertThat(gameTippJson.getTipp().getGuestGoals()).isEqualTo(0);
        assertThat(tipp.getPoints()).isEqualTo(10L);
        assertThat(gameTippJson.getPoints()).isEqualTo(10L);
    }

    @Test
    void testGameTippJsonMapperLostPoints() {
        User user = new User();
        user.setNickname(Nickname.of("Frosch"));

        Game game = new Game();
        game.setHomeTeam(new Team());
        game.setGuestTeam(new Team());
        game.setResult(new GameResult(2, 1));
        game.setPlayed(true);

        GameTipp tipp = new GameTipp();
        tipp.setToken("Token");
        tipp.setUser(user);
        tipp.setGame(game);
        tipp.setTipp(GameResult.of(1, 2), TippStatusType.USER);

        GameTippJsonMapper gameTippJsonMapper = new GameTippJsonMapper();
        GameTippJson gameTippJson = gameTippJsonMapper.map(tipp, new GameTippJson());

        assertThat(gameTippJson.getNickname()).isEqualTo("Frosch");
        assertThat(gameTippJson.getTipp().getHomeGoals()).isEqualTo(1);
        assertThat(gameTippJson.getTipp().getGuestGoals()).isEqualTo(2);
        assertThat(tipp.getPoints()).isEqualTo(0L);
        assertThat(gameTippJson.getPoints()).isEqualTo(0L);
    }

}
