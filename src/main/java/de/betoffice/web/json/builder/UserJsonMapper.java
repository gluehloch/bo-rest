/*
 * ============================================================================
 * Project betoffice-jweb Copyright (c) 2013-2022 by Andre Winkler. All rights
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
import java.util.stream.Collectors;

import de.betoffice.web.json.UserJson;
import de.winkler.betoffice.storage.User;
import de.winkler.betoffice.storage.UserResult;

/**
 * Map {@link User} to {@link UserJson}.
 * 
 * @author Andre Winkler
 */
public class UserJsonMapper {

    public static UserJson map(UserResult userResult, UserJson userJson) {
        userJson.setId(userResult.getUser().getId());
        userJson.setNickname(userResult.getUser().getNickname().value());
        userJson.setWin(userResult.getUserWin());
        userJson.setToto(userResult.getUserTotoWin());
        userJson.setTicket(userResult.getTicket());
        userJson.setPoints(userResult.getPoints());
        userJson.setPosition(userResult.getTabPos());
        return userJson;
    }

    public static List<UserJson> map(List<UserResult> userResults) {
        return userResults.stream().map((userResult) -> {
            UserJson json = new UserJson();
            json = map(userResult, json);
            return json;
        }).collect(Collectors.toList());
    }
    
    private static UserJson map(UserResult userResult) {
    	return map(userResult, new UserJson());
    }

}
