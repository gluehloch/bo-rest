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
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.betoffice.web.BetofficeHttpConsts;
import de.betoffice.web.json.UserProfileJson;
import de.betoffice.web.json.builder.UserProfileJsonMapper;
import de.betoffice.web.task.SendUserProfileChangeMailNotification;
import de.winkler.betoffice.service.CommunityService;
import de.winkler.betoffice.storage.Nickname;

@CrossOrigin
@RestController
@RequestMapping("/office")
public class UserProfileController {

    private final CommunityService communityService;
    private final ValidateSessionService validateSessionService;
    private final SendUserProfileChangeMailNotification sendUserProfileChangeMailNotification;

    public UserProfileController(
            final CommunityService communityService,
            final ValidateSessionService validateSessionService,
            final SendUserProfileChangeMailNotification sendUserProfileChangeMailNotification) {
        this.communityService = communityService;
        this.validateSessionService = validateSessionService;
        this.sendUserProfileChangeMailNotification = sendUserProfileChangeMailNotification;
    }

    @Secured({ "ROLE_TIPPER", "ROLE_ADMIN" })
    @PreAuthorize("@betofficeAuthorizationService.validateSession(#headerToken, #nickname)")
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
    @PreAuthorize("@betofficeAuthorizationService.validateSession(#headerToken, #nickname)")
    @PutMapping(value = "/profile/{nickname}", headers = { "Content-type=application/json" })
    public ResponseEntity<UserProfileJson> updateProfile(
            @PathVariable("nickname") String nickname,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String headerToken,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String headerNickname,
            @RequestBody UserProfileJson userProfileJson) {

        return communityService.findUser(Nickname.of(headerNickname)).map(u -> {
            u.setName(userProfileJson.getName());
            u.setSurname(userProfileJson.getSurname());
            u.setPhone(userProfileJson.getPhone());

            // TODO Das wäre ein schönes Beispiel für Spring-Modulith: Event 'Passwort ändern' und 
            // entsprechender Komponente die auf das Ereignis reagiert.
            if (!StringUtils.equals(u.getEmail(), userProfileJson.getMail())) {
                u.setChangeEmail(userProfileJson.getMail());
                u.setChangeToken(UUID.randomUUID().toString());
                sendUserProfileChangeMailNotification.send(u);
            }
            communityService.updateUser(u);
            return ResponseEntity.of(Optional.of(UserProfileJsonMapper.map(u)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Secured({ "ROLE_TIPPER", "ROLE_ADMIN" })
    @PreAuthorize("@betofficeAuthorizationService.validateSession(#headerToken, #nickname)")
    @PostMapping(value = "/profile/confirm-update/{changeToken}", headers = { "Content-type=application/json" })
    public ResponseEntity<UserProfileJson> confirmUpdateProfile(@PathVariable("changeToken") String changeToken) {
        return communityService.findUserByChangeToken(changeToken).map(u -> {
            u.setEmail(u.getChangeEmail());
            u.setChangeEmail(null);
            u.setChangeToken(null);
            communityService.updateUser(u);
            return ResponseEntity.of(Optional.of(UserProfileJsonMapper.map(u)));
        }).orElse(ResponseEntity.notFound().build());
    }

}
