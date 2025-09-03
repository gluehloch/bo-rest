/*
 * ============================================================================
 * Project betoffice-jweb Copyright (c) 2015-2024 by Andre Winkler. All rights
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

package de.betoffice.web.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.betoffice.web.BetofficeHttpConsts;
import de.betoffice.web.BetofficeUrlPath;
import de.betoffice.web.json.SecurityTokenJson;
import de.winkler.betoffice.service.SecurityToken;

/**
 * Google OAuth2 authentication controller.
 * Handles Google IAM authentication flow.
 */
@CrossOrigin
@RestController
@RequestMapping(BetofficeUrlPath.URL_AUTHENTICATION)
public class GoogleOAuth2Controller {

    private final GoogleIamAuthenticationService googleIamAuthenticationService;

    @Autowired
    public GoogleOAuth2Controller(GoogleIamAuthenticationService googleIamAuthenticationService) {
        this.googleIamAuthenticationService = googleIamAuthenticationService;
    }

    /**
     * Handle successful Google OAuth2 authentication.
     * This endpoint is called after successful Google authentication.
     */
    @GetMapping("/google/callback")
    public ResponseEntity<SecurityTokenJson> googleCallback(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestHeader(required = false, name = BetofficeHttpConsts.HTTP_HEADER_USER_AGENT, defaultValue = BetofficeHttpConsts.HTTP_HEADER_USER_AGENT_UNKNOWN) String userAgent,
            HttpServletRequest request) {

        if (principal == null) {
            return ResponseEntity.unauthorized().build();
        }

        SecurityTokenJson securityToken = googleIamAuthenticationService.authenticateWithGoogle(
                principal,
                request.getSession().getId(),
                request.getRemoteAddr(),
                userAgent);

        // Store security token in session
        HttpSession session = request.getSession();
        session.setAttribute(SecurityToken.class.getName(), securityToken);

        return ResponseEntity.ok(securityToken);
    }

    /**
     * Get current Google authentication status.
     */
    @GetMapping("/google/status")
    public ResponseEntity<SecurityTokenJson> googleStatus(
            @AuthenticationPrincipal OAuth2User principal,
            HttpServletRequest request) {

        if (principal == null) {
            return ResponseEntity.unauthorized().build();
        }

        HttpSession session = request.getSession();
        SecurityTokenJson token = (SecurityTokenJson) session.getAttribute(SecurityToken.class.getName());
        
        if (token != null) {
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.unauthorized().build();
        }
    }
}