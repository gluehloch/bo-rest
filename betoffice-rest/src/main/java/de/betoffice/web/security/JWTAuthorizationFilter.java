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

import static de.betoffice.web.security.SecurityConstants.HEADER_AUTHORIZATION;
import static de.betoffice.web.security.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import de.betoffice.service.AuthService;
import de.betoffice.storage.session.entity.Session;
import de.betoffice.storage.user.RoleType;

/**
 * Autorisierungsfilter.
 *
 * @author Andre Winkler
 */
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(JWTAuthorizationFilter.class);

    private final AuthService authService;

    public JWTAuthorizationFilter(AuthenticationManager authManager, AuthService authService) {
        super(authManager);
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        String header = req.getHeader(HEADER_AUTHORIZATION);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = SecurityConstants.getToken(request);
        if (token != null) {
            Optional<Session> validateSession = authService.validateSession(token);

            if (validateSession.isPresent()) {
                List<RoleType> roleTypes = validateSession.get().getUser().getRoleTypes();

                // TODO Spring-Security benoetigt das Prefix "ROLE_". Sonst werden die Rollen nicht identifiziert.
                List<SimpleGrantedAuthority> authorities = roleTypes.stream()
                        .map(RoleType::name)
                        .map(role -> "ROLE_" + role)
                        .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

                String nickname = validateSession.get().getNickname();
                return new UsernamePasswordAuthenticationToken(nickname, null, authorities);
            }
            return null;
        }
        return null;
    }

}
