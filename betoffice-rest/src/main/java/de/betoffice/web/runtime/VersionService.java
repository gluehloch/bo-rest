package de.betoffice.web.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class VersionService {

    private static final String POM_PROPERTIES_PATH = "classpath:META-INF/maven/de.betoffice/office-web/pom.properties";
    
    private static final Logger LOG = LoggerFactory.getLogger(VersionService.class);
    
    private final ResourceLoader resourceLoader;
    
    public VersionService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Optional<VersionInfo> versionInfo() {
        final Resource res = resourceLoader.getResource(POM_PROPERTIES_PATH);
        final Properties p = new Properties();
        try (InputStream in = res.getInputStream()) {
            p.load(in);
            final VersionInfo info = new VersionInfo(
                    p.getProperty("groupId"),
                    p.getProperty("artifactId"),
                    p.getProperty("version"));
            return Optional.of(info);
        } catch (IOException ex) {
            LOG.error("Failed to load pom.properties from classpath", ex);
            return Optional.empty();
        }
    }
    
    public static final record VersionInfo(String groupId, String artifactId, String version) {
    }

}
