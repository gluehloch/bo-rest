package de.betoffice.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test fuer das Auslesen der Versionsinformationen.
 * 
 * @author Andre Winkler
 */
public class VersionTest {

    @Test
    public void readVersion() {
        VersionService versionService = new VersionService();
        PingDateTime pingDateTime = versionService.version();
        
        assertThat(pingDateTime.getGroupId()).startsWith("de.betoffice");
        assertThat(pingDateTime.getArtifactId()).startsWith("betoffice");
    }
    
}
