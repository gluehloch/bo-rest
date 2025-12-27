/*
 * ============================================================================
 * Project betoffice-jweb Copyright (c) 2013-2022 by Andre Winkler.
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

import de.betoffice.storage.season.entity.Season;
import de.betoffice.storage.season.entity.SeasonReference;
import de.betoffice.storage.user.UserResult;
import de.betoffice.storage.user.entity.Nickname;
import de.betoffice.storage.user.entity.User;
import de.betoffice.web.json.UserJson;

/**
 * Test for {@link UserJsonMapper}.
 * 
 * @author Andre Winkler
 */
class UserJsonMapperTest {

    @Test
    void testUserJsonMapping() {
        User user = new User(Nickname.of("Frosch"));
        Season season = new Season();
        season.setReference(SeasonReference.of("2017/2018", "Bundesliga"));

        UserResult userResult = new UserResult(user);
        userResult.setTabPos(1);
        userResult.setTicket(2);
        userResult.setUserTotoWin(3);
        userResult.setUserWin(4);

        UserJsonMapper userJsonMapper = new UserJsonMapper();
        UserJson userJson = userJsonMapper.map(userResult, new UserJson());

        assertThat(userJson.getNickname()).isEqualTo("Frosch");
        // (13 * win) + (10 * totoWin)
        assertThat(userJson.getPoints()).isEqualTo(82L);
        assertThat(userJson.getPosition()).isEqualTo(1);
        assertThat(userJson.getTicket()).isEqualTo(2);
        assertThat(userJson.getToto()).isEqualTo(3);
        assertThat(userJson.getWin()).isEqualTo(4);
    }

}
