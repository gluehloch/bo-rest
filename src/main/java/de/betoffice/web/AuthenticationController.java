package de.betoffice.web;

import de.betoffice.web.json.SecurityTokenJson;
import de.winkler.betoffice.service.SecurityToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Authentication controller: Login and logout.
 */
@RestController
@RequestMapping("/office")
public class AuthenticationController {

    @Autowired
    private BetofficeAuthenticationService betofficeAuthenticationService;

    @CrossOrigin
    @RequestMapping(value = "/login", method = RequestMethod.POST, headers = { "Content-type=application/json" })
    public ResponseEntity<SecurityTokenJson> login(@RequestBody AuthenticationForm authenticationForm,
                                                   @RequestHeader(required = false, name = BetofficeHttpConsts.HTTP_HEADER_USER_AGENT, defaultValue = BetofficeHttpConsts.HTTP_HEADER_USER_AGENT_UNKNOWN) String userAgent,
                                                   HttpServletRequest request) {

        SecurityTokenJson securityToken = betofficeAuthenticationService.login(authenticationForm.getNickname(),
                authenticationForm.getPassword(), request.getSession().getId(), request.getRemoteAddr(), userAgent);

        HttpSession session = request.getSession();
        session.setAttribute(SecurityToken.class.getName(), securityToken);

        return ResponseEntity.ok(securityToken);
    }

    @CrossOrigin
    @RequestMapping(value = "/logout", method = RequestMethod.POST, headers = { "Content-type=application/json" })
    public SecurityTokenJson logout(@RequestBody LogoutFormData logoutFormData, HttpServletRequest request) {

        SecurityTokenJson securityTokenJson = betofficeAuthenticationService.logout(logoutFormData.getNickname(),
                logoutFormData.getToken());

        HttpSession session = request.getSession();
        session.removeAttribute(SecurityToken.class.getName());

        return securityTokenJson;
    }

}
