/*
 * ============================================================================
 * Project betoffice-jweb Copyright (c) 2015-2021 by Andre Winkler. All rights
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

package de.betoffice.web;

import java.time.ZonedDateTime;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.betoffice.web.json.PingJson;
import de.betoffice.web.json.SecurityTokenJson;
import de.betoffice.web.security.SecurityConstants;
import de.winkler.betoffice.service.SecurityToken;

/**
 * Authentication controller: Login and logout.
 */
@CrossOrigin
@RestController
@RequestMapping(BetofficeUrlPath.URL_AUTHENTICATION)
public class AuthenticationController {

    private final BetofficeAuthenticationService betofficeAuthenticationService;

    @Autowired
    public AuthenticationController(BetofficeAuthenticationService authenticationService) {
        this.betofficeAuthenticationService = authenticationService;
    }

    @RequestMapping(value = BetofficeUrlPath.URL_AUTHENTICATION_PING, method = RequestMethod.GET)
    public PingJson ping() {
        PingJson pingJson = new PingJson();
        pingJson.setDateTime(ZonedDateTime.now());
        return pingJson;
    }

    @PostMapping(value = BetofficeUrlPath.URL_AUTHENTICATION_LOGIN, headers = { "Content-type=application/json" })
    public ResponseEntity<SecurityTokenJson> login(@RequestBody AuthenticationForm authenticationForm,
                                                   @RequestHeader(required = false, name = BetofficeHttpConsts.HTTP_HEADER_USER_AGENT, defaultValue = BetofficeHttpConsts.HTTP_HEADER_USER_AGENT_UNKNOWN) String userAgent,
                                                   HttpServletRequest request) {

        SecurityTokenJson securityToken = betofficeAuthenticationService.login(authenticationForm.getNickname(),
                authenticationForm.getPassword(), request.getSession().getId(), request.getRemoteAddr(), userAgent);

        HttpSession session = request.getSession();
        session.setAttribute(SecurityToken.class.getName(), securityToken);

        return ResponseEntity.ok(securityToken);
    }

    @PostMapping(value = BetofficeUrlPath.URL_AUTHENTICATION_LOGOUT /*, headers = { "Content-type=application/json" }*/)
    public SecurityTokenJson logout(@RequestBody LogoutFormData logoutFormData,
                                    @RequestHeader(required = false, name = SecurityConstants.HEADER_AUTHORIZATION) String authorization,
                                    HttpServletRequest request) {

        SecurityTokenJson securityTokenJson = betofficeAuthenticationService.logout(logoutFormData.getNickname(), logoutFormData.getToken());

        HttpSession session = request.getSession();
        session.removeAttribute(SecurityToken.class.getName());
        
        // TODO
        session.invalidate();
        SecurityContextHolder.clearContext();
        new SecurityContextLogoutHandler().logout(request, null, null);
        
        return securityTokenJson;
    }

}
