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
import java.util.function.Function;

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

import de.betoffice.service.CommunityService;
import de.betoffice.storage.user.entity.Nickname;
import de.betoffice.storage.user.entity.User;
import de.betoffice.validation.ServiceResult;
import de.betoffice.validation.ValidationMessages;
import de.betoffice.web.BetofficeHttpConsts;
import de.betoffice.web.json.UserProfileJson;
import de.betoffice.web.json.builder.UserProfileJsonMapper;

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
                        userProfileJson.isEmailNotificationEnabled(),
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
    public ResponseEntity<RestResult<UserProfileJson>> confirmUpdateProfile(@PathVariable("nickname") String nickname,
            @PathVariable("changeToken") String changeToken, @RequestBody String changeTokenAsBody,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String headerToken,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String headerNickname) {

        final Optional<User> optionalUser = communityService.findUserByChangeToken(changeToken);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final User user = optionalUser.get();
        final ServiceResult<User> confirmMailAddressChangeServiceResult = communityService
                .confirmMailAddressChange(user.getNickname(), changeToken);

        return ResponseEntity.ofNullable(RestResult.of(
                confirmMailAddressChangeServiceResult, UserProfileJsonMapper::map));
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

    public static class RestResult<T> {
        private final boolean successful;
        private final T result;
        private final ValidationMessages messages;

        public static <T, R> RestResult<T> of(final ServiceResult<R> serviceResult, final Function<R, T> mapper) {
            if (serviceResult.isSuccessful()) {
                final R resultObject = serviceResult.result().get();
                final T mappedResultObject = mapper.apply(resultObject);
                return new RestResult<T>(mappedResultObject, serviceResult.messages(), true);
            } else {
                return new RestResult<T>(null, serviceResult.messages(), false);
            }
        }

        private RestResult(final T result, final ValidationMessages messages, final boolean successful) {
            this.result = result;
            this.messages = messages;
            this.successful = successful;
        }

        public T getResult() {
            return result;
        }

        public ValidationMessages getMessages() {
            return messages;
        }

        public ResponseEntity<RestResult<T>> toResponseEntity() {
            if (successful) {
                return ResponseEntity.ok(this);
            } else {
                return ResponseEntity.badRequest().body(this);
            }
        }
    }

}
