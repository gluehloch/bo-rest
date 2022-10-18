package de.betoffice.web;

import de.betoffice.web.json.SecurityTokenJson;
import de.winkler.betoffice.service.AuthService;
import de.winkler.betoffice.service.DateTimeProvider;
import de.winkler.betoffice.service.MasterDataManagerService;
import de.winkler.betoffice.service.SecurityToken;
import de.winkler.betoffice.storage.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefaultBetofficeAuthenticationService implements BetofficeAuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultBetofficeAuthenticationService.class);

    @Autowired
    private DateTimeProvider dateTimeProvider;

    @Autowired
    private AuthService authService;

    @Autowired
    private MasterDataManagerService masterDataManagerService;

    @Override
    public SecurityTokenJson login(String user, String password, String sessionId, String address, String browserId) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Try to login: {}", user);
        }

        SecurityToken securityToken = authService.login(user, password,
                sessionId, address, browserId);

        SecurityTokenJson stj = null;
        if (securityToken == null) {
            stj = new SecurityTokenJson();
            stj.setLoginTime(dateTimeProvider.currentDateTime());
            stj.setNickname(user);
            stj.setRole("no_authorization");
            stj.setToken("no_authorization");
        } else {
            stj = JsonBuilder.toJson(securityToken);
            if (LOG.isInfoEnabled()) {
                LOG.info("Login successful: user=[{}], token=[{}]", user, stj);
            }
        }
        return stj;
    }

    @Override
    public SecurityTokenJson logout(String nickname, String token) {
        Optional<User> user = masterDataManagerService.findUserByNickname(nickname);
        SecurityToken securityToken = new SecurityToken(token, user.get(), user.get().getRoleTypes(), dateTimeProvider.currentDateTime());
        authService.logout(securityToken);

        return JsonBuilder.toJson(securityToken);
    }

}
