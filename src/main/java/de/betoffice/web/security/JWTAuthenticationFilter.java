/*
 * ============================================================================
 * Project betoffice-jweb
 * Copyright (c) 2000-2021 by Andre Winkler. All rights reserved.
 * ============================================================================
 *          GNU GENERAL PUBLIC LICENSE
 *  TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package de.betoffice.web.security;

import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import de.betoffice.web.BetofficeHttpConsts;
import de.winkler.betoffice.service.AuthService;
import de.winkler.betoffice.service.SecurityToken;

/**
 * This filter tries to authenticate the user. So there has to be an instance the {@link de.winkler.betoffice.storage.User} as JSON
 * string in the HTTP request.
 *
 * @author Andre Winkler
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String nickname = request.getParameter("nickname");
        String password = request.getParameter("password");

        String sessionid = request.getSession().getId();
        String ipaddress = request.getRemoteAddr();
        String browserid = request.getHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT);
        if (browserid == null) {
        	browserid = BetofficeHttpConsts.HTTP_HEADER_USER_AGENT_UNKNOWN;
        }
        
        /*SecurityToken token =*/ authService.login(nickname, password, password, ipaddress, browserid);
        
        // UserEntity creds = new ObjectMapper().readValue(req.getInputStream(), UserEntity.class);

        // TODO Create and add GrantedAuthorities.

        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(nickname, password, new ArrayList<>()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) {
        SecurityToken token = authService.findTokenByNickname(auth.getName()).orElseThrow();
        response.addHeader(SecurityConstants.HEADER_AUTHORIZATION, SecurityConstants.TOKEN_PREFIX + token);
    }

}
