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
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.betoffice.storage.group.entity.GroupType;
import de.betoffice.storage.season.entity.Game;
import de.betoffice.storage.season.entity.GameList;
import de.betoffice.storage.season.entity.GameResult;
import de.betoffice.storage.season.entity.Group;
import de.betoffice.storage.team.entity.Team;
import de.betoffice.storage.tip.GameTipp;
import de.betoffice.storage.tip.TippStatusType;
import de.betoffice.storage.user.entity.Nickname;
import de.betoffice.storage.user.entity.User;
import de.betoffice.web.json.GameJson;
import de.betoffice.web.json.GameTippJson;
import de.betoffice.web.json.JsonBuilder;

/**
 * Test for game and gametipp mapping.
 * 
 * @author Andre Winkler
 */
class GameTippJsonAssemblerTest {

    @Test
    void testGameTippJsonAssembler() {
        List<Game> games = new ArrayList<>();
        Game game = new Game() {
            private static final long serialVersionUID = 2794058674675291216L;
            {
                setId(1L);
            }
        };

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Berlin"));

        GroupType groupType = new GroupType() {
            private static final long serialVersionUID = 1L;
            {
                setId(1L);
                setName("1. Bundesliga");
            }
        };
        Group group = new Group();
        group.setGroupType(groupType);
        game.setGroup(group);
        game.setDateTime(now);
        game.setHomeTeam(new Team("Heim"));
        game.setGuestTeam(new Team("Gast"));
        game.setHalfTimeGoals(new GameResult(1, 1));
        game.setResult(new GameResult(2, 1));
        game.setPlayed(true);
        games.add(game);

        GameList round = new GameList();
        round.addGame(game);

        List<GameTipp> gameTipps = new ArrayList<>();
        GameTipp tipp = new GameTipp();
        tipp.setToken("Token");
        tipp.setUser(new User(Nickname.of("Frosch")));
        tipp.setGame(game);
        tipp.setTipp(GameResult.of(2, 1), TippStatusType.USER);
        gameTipps.add(tipp);

        List<GameJson> gamesAndTipps = JsonBuilder.toJsonWithGamesAndTipps(games, gameTipps);

        assertThat(gamesAndTipps).hasSize(1);
        GameJson gameJson = gamesAndTipps.get(0);
        assertThat(gameJson.getResult().getHomeGoals()).isEqualTo(2);
        assertThat(gameJson.getResult().getGuestGoals()).isEqualTo(1);
        assertThat(gameJson.getHomeTeam().getName()).isEqualTo("Heim");
        assertThat(gameJson.getGuestTeam().getName()).isEqualTo("Gast");
        assertThat(gameJson.getTipps()).hasSize(1);
        GameTippJson gameTippJson = gameJson.getTipps().get(0);
        assertThat(gameTippJson.getNickname()).isEqualTo("Frosch");
        assertThat(gameTippJson.getTipp().getHomeGoals()).isEqualTo(2);
        assertThat(gameTippJson.getTipp().getGuestGoals()).isEqualTo(1);
    }

}
