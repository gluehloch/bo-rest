package de.betoffice.web.json;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Returns a server timestamp.
 * 
 * @author Andre Winkler
 */
public class PingJson implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JsonDateTimeFormat.DATETIME_PATTERN, timezone = JsonDateTimeFormat.TIMZONE)
    private ZonedDateTime dateTime;
    private String dateTimeZone;

    private final ZoneId systemDefaultZoneId = ZoneId.systemDefault();
    private final TimeZone systemDefaultTimeZone = TimeZone.getDefault();
    private final String systemPropertyUserTimezone = System.getProperty("user.timezone");
    private final ZoneOffset zoneOffset = ZonedDateTime.now(systemDefaultZoneId).getOffset();

    public ZoneId getSystemDefaultZoneId() {
        return systemDefaultZoneId;
    }

    public TimeZone getSystemDefaultTimeZone() {
        return systemDefaultTimeZone;
    }

    public String getSystemPropertyUserTimezone() {
        return systemPropertyUserTimezone;
    }

    public ZoneOffset getZoneOffset() {
        return zoneOffset;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public String getDateTimeZone() {
        return dateTimeZone;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
        this.dateTimeZone = dateTime.getZone().getId();
    }

    @Override
    public String toString() {
        return "PingJson [dateTime=" + dateTime + ", dateTimeZone=" + dateTimeZone + "]";
    }

}
