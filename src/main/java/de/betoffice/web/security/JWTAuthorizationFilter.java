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
import java.util.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import de.winkler.betoffice.service.AuthService;
import de.winkler.betoffice.storage.Session;

/**
 * Autorisierungsfilter.
 *
 * @author Andre Winkler
 */
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	private final AuthService authService;

	public JWTAuthorizationFilter(AuthenticationManager authManager, AuthService authService) {
		super(authManager);
		this.authService = authService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {

//		Enumeration<String> headerNames = req.getHeaderNames();
//		Iterator<String> stringIterator = headerNames.asIterator();
//		while (stringIterator.hasNext()) {
//			String next = stringIterator.next();
//			System.out.println("/ " + next);
//		}

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
        String token = request.getHeader(HEADER_AUTHORIZATION);
        if (token != null) {
        	Optional<Session> validateSession = authService.validateSession(token.replace(TOKEN_PREFIX, ""));
            if (validateSession.isPresent()) {
                // Werden wir hier mal Rollen verwenden?
            	//
                // List<RoleEntity> roles = roleRepository.findRoles(nickname.get());
                //
                // TODO
                // * User und Authorities laden und dem Request zuordnen.
                // * Wie setzt sich die Authority zusammen: Aus Privilegien und/oder Rollen?!?
                //
                // List<GrantedAuthority> authorities = roles.stream().map(MyGrantedAuthority::of).collect(Collectors.toList());
            	boolean isAdminUser = validateSession.get().getUser().isAdmin();
            	List<GrantedAuthority> authorities = new ArrayList<>();

            	// TODO
				final String ROLE_PREFIX = "ROLE_";
            	if (isAdminUser) {
            		authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + "ADMIN"));
            		authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + "TIPPER"));
            	} else {
            		authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + "TIPPER"));
            	}

            	String nickname = validateSession.get().getNickname();
                return new UsernamePasswordAuthenticationToken(nickname, null, authorities);
            }
            return null;
        }
        return null;
    }

//	/**
//	 * /* TODO Zuordnung zu {@link RoleEntity} und {@link PrivilegeEntity}.
//	 * 
//	 * Könnte hier ebenfalls ein {@link SimpleGrantedAuthority} sein. Die Rolle ist
//	 * nur eine String Repräsentation.
//	 */
//	public static class MyGrantedAuthority implements GrantedAuthority {
//		private static final long serialVersionUID = 1L;
//
//		private String roleName;
//
//		public static MyGrantedAuthority of(RoleEntity role) {
//			return new MyGrantedAuthority(role.getName());
//		}
//
//		private MyGrantedAuthority(String roleName) {
//			this.roleName = roleName;
//		}
//
//		@Override
//		public String getAuthority() {
//			return roleName;
//		}
//	}

}
