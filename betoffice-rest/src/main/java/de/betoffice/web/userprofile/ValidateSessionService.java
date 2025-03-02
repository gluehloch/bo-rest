package de.betoffice.web.userprofile;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
public class ValidateSessionService {

    @PreAuthorize("@betofficeAuthorizationService.validateSession(#param1, #param2)")
    public void validate(String param1, String param2) {
        System.out.println(param1 + " is valid");
    }

}
