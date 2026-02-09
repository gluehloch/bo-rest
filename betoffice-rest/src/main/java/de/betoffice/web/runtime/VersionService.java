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

    private static final String POM_PROPERTIES_PATH = "classpath:/pom-info.properties";
    private static final String GIT_PROPERTIES_PATH = "classpath:/git.properties";

    private static final Logger LOG = LoggerFactory.getLogger(VersionService.class);

    private final ResourceLoader resourceLoader;

    public VersionService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Optional<GitInfo> gitInfo() {
        final Resource res = resourceLoader.getResource(GIT_PROPERTIES_PATH);
        final Properties p = new Properties();
        try (InputStream in = res.getInputStream()) {
            p.load(in);
            final GitInfo info = new GitInfo(
                    p.getProperty("git.tags"),
                    p.getProperty("git.branch"),
                    p.getProperty("git.build.version"),
                    p.getProperty("git.commit.id.describe"),
                    p.getProperty("git.commit.id.abbrev"),
                    p.getProperty("git.commit.id"),
                    p.getProperty("git.commit.time"),
                    p.getProperty("git.build.time"));
            return Optional.of(info);
        } catch (IOException ex) {
            LOG.error("Failed to load git.properties from classpath", ex);
            return Optional.empty();
        }
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

    public static final record GitInfo(String tags, String branch, String buildVersion, String commitIdDescribe,
            String commitIdAbbrev, String commitId, String commitTime, String buildTime) {
    }

}
