package de.betoffice.web.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.core.user.OAuth2User;

import de.betoffice.service.AuthService;
import de.betoffice.service.CommunityService;
import de.betoffice.service.SecurityToken;
import de.betoffice.storage.time.DateTimeProvider;
import de.betoffice.storage.user.entity.Nickname;
import de.betoffice.storage.user.entity.User;
import de.betoffice.web.json.SecurityTokenJson;

public class GoogleIamAuthenticationServiceTest {

    private static final String TEST_EMAIL = "test@google.com";
    private static final String TEST_GOOGLE_ID = "google123";
    private static final String TEST_NAME = "Test User";
    private static final String TEST_SESSION_ID = "session123";
    private static final String TEST_ADDRESS = "192.168.1.1";
    private static final String TEST_BROWSER_ID = "Chrome";

    @Mock
    private DateTimeProvider dateTimeProvider;

    @Mock
    private AuthService authService;

    @Mock
    private CommunityService communityService;

    private GoogleIamAuthenticationService googleIamAuthenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        googleIamAuthenticationService = new GoogleIamAuthenticationService();
        googleIamAuthenticationService.dateTimeProvider = dateTimeProvider;
        googleIamAuthenticationService.authService = authService;
        googleIamAuthenticationService.communityService = communityService;
    }

    @Test
    void testAuthenticateWithGoogle_ExistingUser() {
        // Arrange
        OAuth2User oauth2User = createMockOAuth2User(TEST_EMAIL, TEST_GOOGLE_ID, TEST_NAME);
        User existingUser = createMockUser(TEST_EMAIL);
        SecurityToken securityToken = createMockSecurityToken();

        when(communityService.findUser(Nickname.of(TEST_EMAIL))).thenReturn(Optional.of(existingUser));
        when(authService.login(any(Nickname.class), eq(TEST_GOOGLE_ID),
                eq(TEST_SESSION_ID), eq(TEST_ADDRESS), eq(TEST_BROWSER_ID)))
                        .thenReturn(securityToken);

        // Act
        SecurityTokenJson result = googleIamAuthenticationService.authenticateWithGoogle(
                oauth2User, TEST_SESSION_ID, TEST_ADDRESS, TEST_BROWSER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_EMAIL, result.getNickname());
        assertNotNull(result.getToken());
    }

    @Test
    void testAuthenticateWithGoogle_MissingEmail() {
        // Arrange
        OAuth2User oauth2User = mock(OAuth2User.class);
        when(oauth2User.getAttribute("email")).thenReturn(null);
        when(oauth2User.getAttribute("sub")).thenReturn(TEST_GOOGLE_ID);
        when(dateTimeProvider.currentDateTime()).thenReturn(ZonedDateTime.now());

        // Act
        SecurityTokenJson result = googleIamAuthenticationService.authenticateWithGoogle(
                oauth2User, TEST_SESSION_ID, TEST_ADDRESS, TEST_BROWSER_ID);

        // Assert
        assertNotNull(result);
        assertEquals("no_authorization", result.getRole());
        assertEquals("no_authorization", result.getToken());
    }

    @Test
    void testAuthenticateWithGoogle_MissingGoogleId() {
        // Arrange
        OAuth2User oauth2User = mock(OAuth2User.class);
        when(oauth2User.getAttribute("email")).thenReturn(TEST_EMAIL);
        when(oauth2User.getAttribute("sub")).thenReturn(null);
        when(dateTimeProvider.currentDateTime()).thenReturn(ZonedDateTime.now());

        // Act
        SecurityTokenJson result = googleIamAuthenticationService.authenticateWithGoogle(
                oauth2User, TEST_SESSION_ID, TEST_ADDRESS, TEST_BROWSER_ID);

        // Assert
        assertNotNull(result);
        assertEquals("no_authorization", result.getRole());
        assertEquals("no_authorization", result.getToken());
    }

    private OAuth2User createMockOAuth2User(String email, String googleId, String name) {
        OAuth2User oauth2User = mock(OAuth2User.class);
        when(oauth2User.getAttribute("email")).thenReturn(email);
        when(oauth2User.getAttribute("sub")).thenReturn(googleId);
        when(oauth2User.getAttribute("name")).thenReturn(name);
        when(oauth2User.getAttributes()).thenReturn(Map.of(
                "email", email,
                "sub", googleId,
                "name", name));
        return oauth2User;
    }

    private User createMockUser(String email) {
        User user = mock(User.class);
        when(user.getNickname()).thenReturn(Nickname.of(email));
        when(user.getEmail()).thenReturn(email);
        return user;
    }

    private SecurityToken createMockSecurityToken() {
        SecurityToken token = mock(SecurityToken.class);
        when(token.getToken()).thenReturn("mock-token");
        when(token.getUser()).thenReturn(createMockUser(TEST_EMAIL));
        when(token.getLoginTime()).thenReturn(ZonedDateTime.now());
        return token;
    }
}