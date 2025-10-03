/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2000-2022 by Andre Winkler. All
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

package de.betoffice.web.auth;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.betoffice.service.AuthService;
import de.betoffice.service.CommunityService;
import de.betoffice.service.SecurityToken;
import de.betoffice.storage.time.DateTimeProvider;
import de.betoffice.storage.user.entity.Nickname;
import de.betoffice.storage.user.entity.User;
import de.betoffice.web.json.JsonBuilder;
import de.betoffice.web.json.SecurityTokenJson;

@Component
public class DefaultBetofficeAuthenticationService implements BetofficeAuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultBetofficeAuthenticationService.class);

    @Autowired
    private DateTimeProvider dateTimeProvider;

    @Autowired
    private AuthService authService;

    @Autowired
    private CommunityService communityService;

    @Override
    public SecurityTokenJson login(String user, String password, String sessionId, String address, String browserId) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Try to login: {}", user);
        }

        Nickname nickname = Nickname.of(user);
        SecurityToken securityToken = authService.login(nickname, password,
                sessionId, address, browserId);

        SecurityTokenJson stj = null;
        if (securityToken == null) {
            stj = new SecurityTokenJson();
            stj.setLoginTime(dateTimeProvider.currentDateTime());
            stj.setNickname(user);
            stj.setRole("no_authorization");
            stj.setToken("no_authorization");
        } else {
            stj = JsonBuilder.toJson(securityToken);
            if (LOG.isInfoEnabled()) {
                LOG.info("Login successful: user=[{}], token=[{}]", user, stj);
            }
        }
        return stj;
    }

    @Override
    public SecurityTokenJson logout(String nickname, String token) {
        Optional<User> user = communityService.findUser(Nickname.of(nickname));
        SecurityToken securityToken = new SecurityToken(token, user.get(), user.get().getRoleTypes(),
                dateTimeProvider.currentDateTime());
        authService.logout(securityToken);

        return JsonBuilder.toJson(securityToken);
    }

}
