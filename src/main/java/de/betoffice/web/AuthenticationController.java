package de.betoffice.web;

import de.betoffice.web.json.PingJson;
import de.betoffice.web.json.SecurityTokenJson;
import de.betoffice.web.security.SecurityConstants;
import de.winkler.betoffice.service.SecurityToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.ZonedDateTime;

/**
 * Authentication controller: Login and logout.
 */
@CrossOrigin
@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    private final BetofficeAuthenticationService betofficeAuthenticationService;

    @Autowired
    public AuthenticationController(BetofficeAuthenticationService authenticationService) {
        this.betofficeAuthenticationService = authenticationService;
    }

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public PingJson ping() {
        PingJson pingJson = new PingJson();
        pingJson.setDateTime(ZonedDateTime.now());
        return pingJson;
    }

    @PostMapping(value = "/login", headers = { "Content-type=application/json" })
    public ResponseEntity<SecurityTokenJson> login(@RequestBody AuthenticationForm authenticationForm,
                                                   @RequestHeader(required = false, name = BetofficeHttpConsts.HTTP_HEADER_USER_AGENT, defaultValue = BetofficeHttpConsts.HTTP_HEADER_USER_AGENT_UNKNOWN) String userAgent,
                                                   HttpServletRequest request) {

        SecurityTokenJson securityToken = betofficeAuthenticationService.login(authenticationForm.getNickname(),
                authenticationForm.getPassword(), request.getSession().getId(), request.getRemoteAddr(), userAgent);

        HttpSession session = request.getSession();
        session.setAttribute(SecurityToken.class.getName(), securityToken);

        return ResponseEntity.ok(securityToken);
    }

    @PostMapping(value = "/logout"/*, headers = { "Content-type=application/json" }*/)
    public SecurityTokenJson logout(@RequestBody LogoutFormData logoutFormData,
                                    @RequestHeader(required = false, name = SecurityConstants.HEADER_AUTHORIZATION) String authorization,
                                    HttpServletRequest request) {

        SecurityTokenJson securityTokenJson = betofficeAuthenticationService.logout(logoutFormData.getNickname(), logoutFormData.getToken());

        HttpSession session = request.getSession();
        session.removeAttribute(SecurityToken.class.getName());

        return securityTokenJson;
    }

}
