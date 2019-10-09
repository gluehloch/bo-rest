package de.betoffice.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Delivers date, time and version informations.
 * 
 * @author Andre Winkler
 */
@Service
public class VersionService {

    private static final Logger LOG = LoggerFactory
            .getLogger(VersionService.class);

    public PingDateTime version() {
        InputStream is = PingDateTime.class
                .getResourceAsStream("/version.properties");

        PingDateTime pdt = null;
        Properties props = new Properties();
        try {
            props.load(is);
            String version = props.getProperty("version", "unknown");
            String groupId = props.getProperty("groupId", "unknown");
            String artifactId = props.getProperty("artifactId", "unknown");

            pdt = new PingDateTime(version, groupId, artifactId);
        } catch (IOException ex) {
            LOG.error("Could not read version.properties.", ex);
        }

        pdt.setDateTime(DateTime.now().toDate());

        return pdt;
    }

}
