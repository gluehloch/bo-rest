package de.betoffice.web.tipp;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.betoffice.service.AuthService;

@Service
@Transactional(readOnly = true)
public class TippAuthorisationService {

    private final AuthService authService;

    public TippAuthorisationService(AuthService authService) {
        this.authService = authService;
    }

    public boolean isSubmissionAllowed(String token, String nickname) {
        return authService.validateSession(token)
                .map(session -> session.getUser().getNickname().value().contentEquals(nickname)
                /*&& session.getNickname().equals(nickname)*/)
                .orElse(false);
    }
}
