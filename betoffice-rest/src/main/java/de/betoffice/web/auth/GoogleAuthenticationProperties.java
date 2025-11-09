package de.betoffice.web.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * TODO
 */
@Profile(value = "dev")
@Configuration
@PropertySource(ignoreResourceNotFound = false, value = { "classpath:/application-google-oauth.properties" })
public class GoogleAuthenticationProperties {

}
