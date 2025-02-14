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

package de.betoffice.web.profile;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.betoffice.web.AccessDeniedException;
import de.betoffice.web.BetofficeHttpConsts;
import de.betoffice.web.json.ProfileJson;
import de.betoffice.web.json.builder.ProfileJsonMapper;
import de.winkler.betoffice.service.AuthService;
import de.winkler.betoffice.service.CommunityService;
import de.winkler.betoffice.storage.Nickname;
import de.winkler.betoffice.storage.Session;

@CrossOrigin
@RestController
@RequestMapping("/office")
public class ProfileController {

    private final AuthService authService;
    private final CommunityService communityService;

    public ProfileController(AuthService authService, CommunityService communityService) {
        this.authService = authService;
        this.communityService = communityService;
    }

    @Secured({ "ROLE_TIPPER", "ROLE_ADMIN" })
    @GetMapping(value = "/profile/{nickname}", headers = { "Content-type=application/json" })
    public ResponseEntity<ProfileJson> findProfile(
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        Session session = authService.validateSession(token).orElseThrow(() -> new AccessDeniedException());
        if (!session.getNickname().equals(nickname)) {
            throw new IllegalStateException();
        }
        return ResponseEntity.of(communityService.findUser(Nickname.of(nickname)).map(ProfileJsonMapper::map));
    }

}
