/*
 * ============================================================================
 * Project betoffice-jweb
 * Copyright (c) 2000-2024 by Andre Winkler. All rights reserved.
 * ============================================================================
 *          GNU GENERAL PUBLIC LICENSE
 *  TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package de.betoffice.web.tipp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.betoffice.service.AuthService;
import de.betoffice.storage.session.entity.Session;
import de.betoffice.storage.user.entity.Nickname;
import de.betoffice.storage.user.entity.User;

/**
 * Unit tests for {@link TippAuthorisationService}.
 * 
 * Tests the authorization logic for tipp submissions, verifying that:
 * - Valid sessions with matching nicknames are authorized
 * - Invalid tokens/sessions are rejected
 * - Mismatched nicknames are rejected
 */
class TippAuthorisationServiceTest {

    private static final String VALID_TOKEN = "valid-token-123";
    private static final String INVALID_TOKEN = "invalid-token-456";
    private static final String NICKNAME = "TestUser";
    private static final String DIFFERENT_NICKNAME = "DifferentUser";

    private AuthService authService;
    private TippAuthorisationService tippAuthorisationService;

    @BeforeEach
    void setup() {
        authService = mock(AuthService.class);
        tippAuthorisationService = new TippAuthorisationService(authService);
    }

    @Test
    void testSuccessfulAuthorization_WhenTokenAndNicknameMatch() {
        // Arrange
        Session mockSession = createMockSession(NICKNAME);
        when(authService.validateSession(VALID_TOKEN)).thenReturn(Optional.of(mockSession));

        // Act
        boolean result = tippAuthorisationService.isSubmissionAllowed(VALID_TOKEN, NICKNAME);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void testFailedAuthorization_WhenSessionIsInvalid() {
        // Arrange
        when(authService.validateSession(INVALID_TOKEN)).thenReturn(Optional.empty());

        // Act
        boolean result = tippAuthorisationService.isSubmissionAllowed(INVALID_TOKEN, NICKNAME);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void testFailedAuthorization_WhenUserNicknameDoesNotMatch() {
        // Arrange
        Session mockSession = createMockSession(DIFFERENT_NICKNAME);
        when(authService.validateSession(VALID_TOKEN)).thenReturn(Optional.of(mockSession));

        // Act
        boolean result = tippAuthorisationService.isSubmissionAllowed(VALID_TOKEN, NICKNAME);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void testFailedAuthorization_WhenSessionNicknameDoesNotMatch() {
        // Arrange
        // Create a session where user.nickname matches but session.nickname doesn't
        // This tests the redundant check: session.getNickname().equals(nickname)
        Session mockSession = createMockSessionWithDifferentSessionNickname(NICKNAME, DIFFERENT_NICKNAME);
        when(authService.validateSession(VALID_TOKEN)).thenReturn(Optional.of(mockSession));

        // Act
        boolean result = tippAuthorisationService.isSubmissionAllowed(VALID_TOKEN, NICKNAME);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void testFailedAuthorization_WhenNullToken() {
        // Arrange
        when(authService.validateSession(null)).thenReturn(Optional.empty());

        // Act
        boolean result = tippAuthorisationService.isSubmissionAllowed(null, NICKNAME);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void testFailedAuthorization_WhenNullNickname() {
        // Arrange
        Session mockSession = createMockSession(NICKNAME);
        when(authService.validateSession(VALID_TOKEN)).thenReturn(Optional.of(mockSession));

        // Act
        boolean result = tippAuthorisationService.isSubmissionAllowed(VALID_TOKEN, null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void testFailedAuthorization_WhenEmptyNickname() {
        // Arrange
        Session mockSession = createMockSession(NICKNAME);
        when(authService.validateSession(VALID_TOKEN)).thenReturn(Optional.of(mockSession));

        // Act
        boolean result = tippAuthorisationService.isSubmissionAllowed(VALID_TOKEN, "");

        // Assert
        assertThat(result).isFalse();
    }

    /**
     * Creates a mock session with matching user nickname and session nickname.
     */
    private Session createMockSession(String nickname) {
        Session session = mock(Session.class);
        User user = mock(User.class);
        Nickname nicknameObj = mock(Nickname.class);

        when(nicknameObj.value()).thenReturn(nickname);
        when(user.getNickname()).thenReturn(nicknameObj);
        when(session.getUser()).thenReturn(user);
        when(session.getNickname()).thenReturn(nickname);

        return session;
    }

    /**
     * Creates a mock session where user.nickname and session.nickname differ.
     * This helps test the redundant nickname check in the authorization logic.
     */
    private Session createMockSessionWithDifferentSessionNickname(String userNickname, String sessionNickname) {
        Session session = mock(Session.class);
        User user = mock(User.class);
        Nickname nicknameObj = mock(Nickname.class);

        when(nicknameObj.value()).thenReturn(userNickname);
        when(user.getNickname()).thenReturn(nicknameObj);
        when(session.getUser()).thenReturn(user);
        when(session.getNickname()).thenReturn(sessionNickname);

        return session;
    }
}
