package de.betoffice.web.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * TODO
 */
@Profile({ "dev", "test" })
@Configuration
@PropertySource(ignoreResourceNotFound = false, value = { "classpath:/oauth.properties" })
public class OAuthProperties {

}
