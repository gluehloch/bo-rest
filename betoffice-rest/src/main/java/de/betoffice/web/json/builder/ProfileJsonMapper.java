/*
 * ============================================================================
 * Project betoffice-jweb Copyright (c) 2016-2025 by Andre Winkler. All rights
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

import java.util.List;

import de.betoffice.web.json.PartyJson;
import de.betoffice.web.json.ProfileJson;
import de.winkler.betoffice.storage.Nickname;
import de.winkler.betoffice.storage.User;

/**
 * Map {@link User} to {@link PartyJson}.
 * 
 * @author Andre Winkler
 */
public class ProfileJsonMapper {

    public static ProfileJson map(User user) {
        return map(user, new ProfileJson());
    }

    public static ProfileJson map(User user, ProfileJson partyJson) {
        partyJson.setName(user.getName());
        partyJson.setSurname(user.getSurname());
        partyJson.setMail(user.getEmail());
        partyJson.setNickname(user.getNickname().value());
        partyJson.setPhone(user.getPhone());
        return partyJson;
    }

    public static List<ProfileJson> map(List<User> users) {
        return users.stream().map(ProfileJsonMapper::map).toList();
    }

    public static User reverse(ProfileJson partyJson, User user) {
        user.setName(partyJson.getName());
        user.setSurname(partyJson.getSurname());
        user.setEmail(partyJson.getMail());
        user.setNickname(Nickname.of(partyJson.getNickname()));
        user.setPhone(partyJson.getPhone());
        return user;
    }

}
