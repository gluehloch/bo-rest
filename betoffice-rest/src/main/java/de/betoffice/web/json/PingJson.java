package de.betoffice.web.json;

import java.io.Serializable;
import java.time.ZonedDateTime;

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
