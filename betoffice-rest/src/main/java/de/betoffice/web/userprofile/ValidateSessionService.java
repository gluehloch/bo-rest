package de.betoffice.web.userprofile;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
public class ValidateSessionService {

    @PreAuthorize("@betofficeAuthorizationService.validateSession(#token, #nickname)")
    public void validate(String token, String nickname) {
        System.out.println(nickname + " is valid");
    }

}
