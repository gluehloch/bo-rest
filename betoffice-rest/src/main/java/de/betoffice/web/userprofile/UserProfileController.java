/*
 * ============================================================================
 * Project betoffice-jweb Copyright (c) 2015-2026 by Andre Winkler. All rights
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
import org.springframework.web.bind.annotation.RestController;

import de.betoffice.service.UserService;
import de.betoffice.service.request.UserUpdateCommand;
import de.betoffice.storage.user.entity.Nickname;
import de.betoffice.storage.user.entity.UserEntity;
import de.betoffice.storage.user.entity.UserProfileDto;
import de.betoffice.storage.user.entity.UserProfileDtoMapper;
import de.betoffice.validation.ServiceResult;
import de.betoffice.web.BetofficeHttpConsts;
import de.betoffice.web.ResponseEntityBuilder;
import de.betoffice.web.RestResult;

@CrossOrigin
@RestController
@RequestMapping("/office")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(final UserService userService) {
        this.userService = userService;
    }

    @Secured({ "ROLE_TIPPER", "ROLE_ADMIN" })
    // TODO Diese Prüfung ist überflüssig. War aber auch nur als Proof-of-Concept Lösung gedacht.
    @PreAuthorize("@betofficeAuthorizationService.validateSession(#headerToken, #nickname)")
    @GetMapping(value = "/profile/{nickname}", produces = { "application/json" })
    public ResponseEntity<UserProfileDto> findProfile(@PathVariable("nickname") String nickname,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String headerToken,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String headerNickname) {

        // TODO Session nickname == #nickname

        return ResponseEntity
                .of(userService.findUser(Nickname.of(nickname)).map(UserProfileDtoMapper::map));
    }

    @Secured({ "ROLE_TIPPER", "ROLE_ADMIN" })
    @PostMapping(value = "/profile/{nickname}", headers = { "Content-type=application/json" })
    public ResponseEntity<UserProfileDto> updateProfile(@PathVariable("nickname") String nickname,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String headerToken,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String headerNickname,
            @RequestBody UserProfileDto userProfileJson) {

        final UserUpdateCommand userUpdateCommand = new UserUpdateCommand(
                false,
                Nickname.of(nickname),
                userProfileJson.getName(),
                userProfileJson.getSurname(),
                userProfileJson.getMail(),
                userProfileJson.isEmailNotificationEnabled(),
                userProfileJson.getPhone());

        final ServiceResult<UserProfileDto> update = userService.update(userUpdateCommand);
        return ResponseEntityBuilder.toResponseEntity(update);
    }

    @Secured({ "ROLE_TIPPER", "ROLE_ADMIN" })
    @PostMapping(value = "/profile/{nickname}/resubmit-confirmation-mail", headers = {
            "Content-type=application/json" })
    public ResponseEntity<UserProfileDto> resubmitConfirmationMail(@PathVariable("nickname") String nickname,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String headerToken,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String headerNickname) {

        return ResponseEntity.of(UserProfileDtoMapper.map(
                userService.resubmitConfirmationMail(Nickname.of(nickname))));
    }

    @Secured({ "ROLE_TIPPER", "ROLE_ADMIN" })
    @PostMapping(value = "/profile/{nickname}/confirm-update/{changeToken}", headers = {
            "Content-type=text/plain" })
    public ResponseEntity<RestResult<UserProfileDto>> confirmUpdateProfile(@PathVariable("nickname") String nickname,
            @PathVariable("changeToken") String changeToken, @RequestBody String changeTokenAsBody,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String headerToken,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String headerNickname) {

        final Optional<UserEntity> optionalUser = userService.findUserByChangeToken(changeToken);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final UserEntity user = optionalUser.get();
        final ServiceResult<UserEntity> confirmMailAddressChangeServiceResult = userService
                .confirmMailAddressChange(user.getNickname(), changeToken);

        return ResponseEntity.ofNullable(RestResult.of(
                confirmMailAddressChangeServiceResult, UserProfileDtoMapper::map));
    }

    @Secured({ "ROLE_TIPPER", "ROLE_ADMIN" })
    @PostMapping(value = "/profile/{nickname}/abort-update", headers = {
            "Content-type=text/plain" })
    public ResponseEntity<UserProfileDto> abortUpdateProfile(@PathVariable("nickname") String nickname,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String headerToken,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String headerNickname) {

        return ResponseEntity.of(UserProfileDtoMapper.map(userService.findUser(Nickname.of(nickname))
                .flatMap(u -> userService.abortMailAddressChange(u.getNickname()))));
    }

}
