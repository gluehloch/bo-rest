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
import org.springframework.web.bind.annotation.RestController;

import de.betoffice.web.BetofficeHttpConsts;
import de.betoffice.web.json.UserProfileJson;
import de.betoffice.web.json.builder.UserProfileJsonMapper;
import de.winkler.betoffice.service.CommunityService;
import de.winkler.betoffice.storage.Nickname;

@CrossOrigin
@RestController
@RequestMapping("/office")
public class UserProfileController {

    private final CommunityService communityService;

    public UserProfileController(final CommunityService communityService) {
        this.communityService = communityService;
    }

    @Secured({ "ROLE_TIPPER", "ROLE_ADMIN" })
    // TODO Diese Prüfung ist überflüssig. War aber auch nur als Proof-of-Concept Lösung gedacht.
    @PreAuthorize("@betofficeAuthorizationService.validateSession(#headerToken, #nickname)")
    @GetMapping(value = "/profile/{nickname}", headers = { "Content-type=application/json" })
    public ResponseEntity<UserProfileJson> findProfile(@PathVariable("nickname") String nickname,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String headerToken,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String headerNickname) {

        return ResponseEntity
                .of(communityService.findUser(Nickname.of(headerNickname)).map(UserProfileJsonMapper::map));
    }

    @Secured({ "ROLE_TIPPER", "ROLE_ADMIN" })
    @PostMapping(value = "/profile/{nickname}", headers = { "Content-type=application/json" })
    public ResponseEntity<UserProfileJson> updateProfile(@PathVariable("nickname") String nickname,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String headerToken,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String headerNickname,
            @RequestBody UserProfileJson userProfileJson) {

        return ResponseEntity.of(UserProfileJsonMapper.map(
                communityService.updateUser(
                        false,
                        Nickname.of(nickname),
                        userProfileJson.getName(),
                        userProfileJson.getSurname(),
                        userProfileJson.getMail(),
                        userProfileJson.getPhone())));
    }

    @Secured({ "ROLE_TIPPER", "ROLE_ADMIN" })
    @PostMapping(value = "/profile/{nickname}/resubmit-confirmation-mail", headers = {
            "Content-type=application/json" })
    public ResponseEntity<UserProfileJson> resubmitConfirmationMail(@PathVariable("nickname") String nickname,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String headerToken,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String headerNickname) {

        return ResponseEntity.of(UserProfileJsonMapper.map(
                communityService.resubmitConfirmationMail(Nickname.of(nickname))));
    }

    @Secured({ "ROLE_TIPPER", "ROLE_ADMIN" })
    @PostMapping(value = "/profile/{nickname}/confirm-update/{changeToken}", headers = {
            "Content-type=text/plain" })
    public ResponseEntity<UserProfileJson> confirmUpdateProfile(@PathVariable("nickname") String nickname,
            @PathVariable("changeToken") String changeToken, @RequestBody String changeTokenAsBody,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String headerToken,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String headerNickname) {

        return ResponseEntity.of(UserProfileJsonMapper.map(communityService.findUserByChangeToken(changeToken)
                .flatMap(u -> communityService.confirmMailAddressChange(u.getNickname(), changeToken))));
    }

    @Secured({ "ROLE_TIPPER", "ROLE_ADMIN" })
    @PostMapping(value = "/profile/{nickname}/abort-update", headers = {
            "Content-type=text/plain" })
    public ResponseEntity<UserProfileJson> abortUpdateProfile(@PathVariable("nickname") String nickname,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String headerToken,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String headerNickname) {

        return ResponseEntity.of(UserProfileJsonMapper.map(communityService.findUser(Nickname.of(nickname))
                .flatMap(u -> communityService.abortMailAddressChange(u.getNickname()))));
    }

}
