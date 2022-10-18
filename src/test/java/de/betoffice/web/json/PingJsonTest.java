package de.betoffice.web.json;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.winkler.betoffice.service.DateTimeProvider;
import de.winkler.betoffice.service.DefaultDateTimeProvider;

public class PingJsonTest {

    private DateTimeProvider dateTimeProvider = new DefaultDateTimeProvider();
    
    @Test
    public void testPingJson() {
        PingJson pingJson = new PingJson();
        pingJson.setDateTime(dateTimeProvider.currentDateTime());
        assertThat(pingJson.getDateTimeZone()).isEqualTo("Europe/Berlin");
    }
    
}
