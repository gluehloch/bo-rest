package de.betoffice.web.auth;

import de.betoffice.web.json.SecurityTokenJson;

public interface BetofficeAuthenticationService {

    /**
     * Login
     *
     * @param user      username
     * @param password  password
     * @param sessionId Java Servlet session id
     * @param address   IP address
     * @param browserId browser id (mozilla/firefox/ie/...)
     * @return a security token
     */
    SecurityTokenJson login(String user, String password, String sessionId, String address, String browserId);

    /**
     * Logout
     *
     * @param nickname username
     * @param token    the token from the security token
     * @return a security token
     */
    SecurityTokenJson logout(String nickname, String token);

}
