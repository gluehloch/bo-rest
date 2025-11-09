# Google IAM Authentication Integration

This document describes the Google IAM (Identity and Access Management) authentication integration for the betoffice-rest application.

## Overview

The Google IAM integration allows users to authenticate using their Google accounts via OAuth2. This is implemented alongside the existing username/password authentication system.

## Features

- OAuth2 authentication with Google
- Automatic user creation for new Google users
- Integration with existing role-based authorization system
- Session management compatible with existing JWT tokens
- RESTful endpoints for Google authentication

## Setup and Configuration

### 1. Google Cloud Console Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google+ API or Google Identity services
4. Go to "Credentials" → "Create Credentials" → "OAuth 2.0 Client IDs"
5. Select "Web application" as the application type
6. Add authorized redirect URIs:
   - For local development: `http://localhost:8080/login/oauth2/code/google`
   - AWI Local development with Spring-Boot (Spielt der Pfad `/bo` für das Redirect eine Rolle?):
      - `http://localhost:7878/bo/office/ping`
      - `http://localhost:7878/login/oauth2/code/google`
   
   - For production: `https://yourdomain.com/login/oauth2/code/google`
7. Note down the Client ID and Client Secret

### 2. Application Configuration

Update your application properties with Google OAuth2 credentials:

```properties
# Google OAuth2 Configuration
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID:your-google-client-id}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET:your-google-client-secret}
spring.security.oauth2.client.registration.google.scope=openid,profile,email
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}

spring.security.oauth2.client.provider.google.authorization-uri=https://accounts.google.com/o/oauth2/v2/auth
spring.security.oauth2.client.provider.google.token-uri=https://oauth2.googleapis.com/token
spring.security.oauth2.client.provider.google.user-info-uri=https://www.googleapis.com/oauth2/v3/userinfo
spring.security.oauth2.client.provider.google.user-name-attribute=sub
```

You can set the credentials as environment variables:
```bash
export GOOGLE_CLIENT_ID="your-actual-client-id"
export GOOGLE_CLIENT_SECRET="your-actual-client-secret"
```

## API Endpoints

### Google Authentication Endpoints

1. **Get Google Login URL**
   - `GET /authentication/google/login-url`
   - Returns the URL to initiate Google OAuth2 authentication
   - Response: `{"url": "http://localhost:8080/oauth2/authorization/google"}`

2. **Google Authentication Callback**
   - `GET /authentication/google/callback`
   - Handles successful Google authentication
   - Returns security token JSON with user information

3. **Google Authentication Status**
   - `GET /authentication/google/status`
   - Returns current Google authentication status
   - Requires authenticated session

### Authentication Flow

1. **Frontend initiates Google login:**
   ```javascript
   // Get Google login URL
   fetch('/authentication/google/login-url')
     .then(response => response.json())
     .then(data => {
       // Redirect user to Google authentication
       window.location.href = data.url;
     });
   ```

2. **User authenticates with Google and is redirected back**

3. **Backend processes the callback and creates session:**
   ```
   GET /authentication/google/callback
   ```

4. **Frontend can check authentication status:**
   ```javascript
   fetch('/authentication/google/status')
     .then(response => response.json())
     .then(securityToken => {
       // User is authenticated with token
       console.log('User:', securityToken.nickname);
       console.log('Role:', securityToken.role);
     });
   ```

## User Management

### New User Creation

When a user authenticates with Google for the first time:
1. The system checks if a user with the email already exists (using email as nickname)
2. If not found, authentication fails with "no_authorization" 
3. **Note**: For the initial implementation, Google users must be manually created in the system first
4. Future enhancement: Automatic user creation can be implemented by extending the CommunityService

### Existing User Mapping

For Google authentication to work, users must be created with:
- Nickname: Same as their Google email address
- The system will authenticate them using their Google ID instead of password

This ensures compatibility with the existing authentication system while adding Google IAM support.

## Security Considerations

1. **Scope Limitation**: Only requests `openid`, `profile`, and `email` scopes
2. **Token Validation**: Uses Google's OAuth2 token validation
3. **Session Security**: Integrates with existing JWT token system
4. **Role Mapping**: Google users get appropriate roles based on your business logic

## Troubleshooting

### Common Issues

1. **"redirect_uri_mismatch" error:**
   - Ensure the redirect URI in Google Cloud Console matches exactly
   - Check for trailing slashes and protocol (http vs https)

2. **"unauthorized_client" error:**
   - Verify Client ID and Client Secret are correct
   - Ensure the Google+ API is enabled

3. **User creation fails:**
   - Check database permissions
   - Verify email uniqueness constraints
   - Review application logs for detailed error messages

### Debug Mode

Enable debug logging for OAuth2:
```properties
logging.level.org.springframework.security.oauth2=DEBUG
logging.level.de.betoffice.web.auth=DEBUG
```

## Integration with Existing System

The Google IAM integration is designed to work alongside the existing authentication system:

- Existing username/password authentication remains functional
- Google-authenticated users get the same security tokens
- Role-based authorization works for both authentication methods
- Session management is unified

## Testing

Run the Google IAM authentication tests:
```bash
mvn test -Dtest=GoogleIamAuthenticationServiceTest
```

The tests cover:
- Successful Google authentication
- Error handling for missing user attributes
- User creation and mapping scenarios