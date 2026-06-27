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

package de.betoffice.web.auth;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import de.betoffice.service.AuthService;
import de.betoffice.service.CommunityService;
import de.betoffice.service.SecurityToken;
import de.betoffice.storage.time.DateTimeProvider;
import de.betoffice.storage.user.entity.Nickname;
import de.betoffice.storage.user.entity.User;
import de.betoffice.web.json.JsonBuilder;
import de.betoffice.web.json.SecurityTokenJson;

/**
 * Google IAM authentication service implementation. Handles OAuth2 authentication with Google Identity and Access
 * Management.
 */
@Component
public class GoogleIamAuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleIamAuthenticationService.class);

    @Autowired
    DateTimeProvider dateTimeProvider;

    @Autowired
    AuthService authService;

    @Autowired
    CommunityService communityService;

    /**
     * Authenticate user via Google OAuth2.
     *
     * @param  oauth2User the OAuth2 user from Google
     * @param  sessionId  Java Servlet session id
     * @param  address    IP address
     * @param  browserId  browser id (mozilla/firefox/ie/...)
     * @return            a security token
     */
    public SecurityTokenJson authenticateWithGoogle(OAuth2User oauth2User, String sessionId, String address,
            String browserId) {
        if (LOG.isInfoEnabled()) {
            LOG.info(String.format("Google OAuth2 authentication for: %s", oauth2User.getAttribute("email")));
        }

        String email = oauth2User.getAttribute("email");
        String googleId = oauth2User.getAttribute("sub");
        String name = oauth2User.getAttribute("name");

        if (email == null || googleId == null) {
            LOG.warn("Missing required Google user attributes: email={}, googleId={}", email, googleId);
            return createFailedAuthenticationToken(email != null ? email : "unknown");
        }

        // Try to find existing user by email (using nickname as email for now)
        Nickname nickname = Nickname.of(email);
        Optional<User> existingUser = communityService.findUser(nickname);

        if (existingUser.isEmpty()) {
            LOG.warn("User not found for Google authentication: {}. User must be created manually.", email);
            return createFailedAuthenticationToken(email);
        }

        User user = existingUser.get();

        // Use existing login method with Google ID as password placeholder
        SecurityToken securityToken = authService.login(nickname, googleId,
                sessionId, address, browserId);

        SecurityTokenJson stj = null;
        if (securityToken == null) {
            LOG.warn("Failed to create security token for Google user: {}", nickname);
            stj = createFailedAuthenticationToken(nickname.value());
        } else {
            stj = JsonBuilder.toJson(securityToken);
            if (LOG.isInfoEnabled()) {
                LOG.info("Google authentication successful: user=[{}], token=[{}]", nickname, stj);
            }
        }
        return stj;
    }

    /**
     * Create a failed authentication token.
     */
    private SecurityTokenJson createFailedAuthenticationToken(String identifier) {
        SecurityTokenJson stj = new SecurityTokenJson();
        stj.setLoginTime(dateTimeProvider.currentDateTime());
        stj.setNickname(identifier);
        stj.setRole("no_authorization");
        stj.setToken("no_authorization");
        return stj;
    }
}