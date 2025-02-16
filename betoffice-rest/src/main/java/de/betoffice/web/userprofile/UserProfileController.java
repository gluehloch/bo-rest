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

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.betoffice.web.BetofficeHttpConsts;
import de.betoffice.web.json.UserProfileJson;
import de.betoffice.web.json.builder.UserProfileJsonMapper;
import de.winkler.betoffice.service.AuthService;
import de.winkler.betoffice.service.CommunityService;
import de.winkler.betoffice.storage.Nickname;
import de.winkler.betoffice.storage.User;

@CrossOrigin
@RestController
@RequestMapping("/office")
public class UserProfileController {

    private final CommunityService communityService;
    private final ValidateSessionService validateSessionService;

    public UserProfileController(CommunityService communityService, ValidateSessionService validateSessionService) {
        this.communityService = communityService;
        this.validateSessionService = validateSessionService;
    }

    @Secured({ "ROLE_TIPPER", "ROLE_ADMIN" })
    @GetMapping(value = "/profile/{nickname}", headers = { "Content-type=application/json" })
    public ResponseEntity<UserProfileJson> findProfile(
            @PathVariable("nickname") String nickname,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String headerToken,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String headerNickname) {

        validateSessionService.validate(headerToken, headerNickname);
        return ResponseEntity
                .of(communityService.findUser(Nickname.of(headerNickname)).map(UserProfileJsonMapper::map));
    }

    @Secured({ "ROLE_TIPPER", "ROLE_ADMIN" })
    //@PreAuthorize("@betofficeAuthorizationService.validateSession(#headerToken, #nickname)")
    @PostMapping(value = "/profile/{nickname}", headers = { "Content-type=application/json" })
    public ResponseEntity<UserProfileJson> updateProfile(
            @PathVariable("nickname") String nickname,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String headerToken,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String headerNickname,
            @RequestBody UserProfileJson userProfileJson) {

        /*
        Optional<User> user = communityService.findUser(Nickname.of(headerNickname));
        user.ifPresent(u -> {
            u.setName(userProfileJson.getName());
            u.setSurname(userProfileJson.getSurname());
            u.setEmail(userProfileJson.getMail());
            u.setPhone(userProfileJson.getPhone());
            communityService.updateUser(u);
        });
         */

         /*
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
             */

        return communityService.findUser(Nickname.of(headerNickname)).map(u -> {
            u.setName(userProfileJson.getName());
            u.setSurname(userProfileJson.getSurname());
            u.setEmail(userProfileJson.getMail());
            u.setPhone(userProfileJson.getPhone());
            communityService.updateUser(u);
            return ResponseEntity.of(UserProfileJsonMapper.map(u));
        }).orElse(
            ResponseEntity.notFound().build()
        );
    }

}
