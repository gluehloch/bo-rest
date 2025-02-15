/*
 * ============================================================================
 * Project betoffice-jweb Copyright (c) 2015-2025 by Andre Winkler. All rights
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

package de.betoffice.web.userprofile;

import java.util.Optional;

import org.springframework.stereotype.Service;

import de.winkler.betoffice.service.AuthService;
import de.winkler.betoffice.storage.Session;

@Service
public class BetofficeAuthorizationService {

    private final AuthService authService;

    public BetofficeAuthorizationService(AuthService authService) {
        this.authService = authService;
    }

    public boolean validateSession(String token, String nickname) {
        Optional<Session> session = authService.validateSession(token);
        if (session.isEmpty())
            return false;
        if (!session.get().getNickname().equals(nickname))
            return false;
        return true;
    }

}
