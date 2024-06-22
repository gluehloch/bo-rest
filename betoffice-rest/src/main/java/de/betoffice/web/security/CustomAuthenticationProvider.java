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
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.winkler.betoffice.service.AuthService;
import de.winkler.betoffice.storage.Nickname;
import de.winkler.betoffice.storage.User;

/**
 * Custom Authentification Provider: Defines my own authentication implementation. A nickname/password comparison.
 *
 * @author Andre Winkler
 */
@Service
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOG = Logger.getLogger(CustomAuthenticationProvider.class.getName());

    private final AuthService authService;

    @Autowired
    public CustomAuthenticationProvider(AuthService authService) {
        this.authService = authService;
    }

    @Transactional
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Nickname nickname = Nickname.of(authentication.getName());
        Object credentials = authentication.getCredentials();

        LOG.info(() -> "credentials class: " + credentials.getClass());

        if (!(credentials instanceof String)) {
            return null;
        }

        String password = credentials.toString();

        // TODO Da muss ich das Authentication Objekt erweitern.
        Object details = authentication.getDetails();
        String ipaddress = null;
        String browserid = null;

        authService.login(nickname, password, password, ipaddress, browserid);

        User user = authService.findByNickname(nickname).orElseThrow(
                () -> new BadCredentialsException(
                        String.format("Authentication failed for nickname=[%1s].", nickname.value())));

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        //
        // TODO Rollen und Benutzer.
        //
        grantedAuthorities.add(new SimpleGrantedAuthority("USER"));

        //
        // TODO ???? UsernamePasswordAuthenticationToken oder lieber was JWT naeheres???
        //
        Authentication auth = new UsernamePasswordAuthenticationToken(nickname, password, grantedAuthorities);

        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
