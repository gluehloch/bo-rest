package de.betoffice.web;

import de.betoffice.web.json.SecurityTokenJson;
import de.betoffice.web.security.SecurityConstants;
import de.winkler.betoffice.service.SecurityToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Authentication controller: Login and logout.
 */
@CrossOrigin
@RestController
@RequestMapping("/office")
public class AuthenticationController {

    @Autowired
    private BetofficeAuthenticationService betofficeAuthenticationService;

    @PostMapping(value = "/login" /*, headers = { "Content-type=application/json" }*/, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
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
