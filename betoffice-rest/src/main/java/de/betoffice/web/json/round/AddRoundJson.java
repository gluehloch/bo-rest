package de.betoffice.web.json.round;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import de.betoffice.web.json.JsonDateTimeFormat;

public class AddRoundJson {

    private Long seasonId;
    private Long groupTypeId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JsonDateTimeFormat.DATETIME_PATTERN, timezone = JsonDateTimeFormat.TIMZONE)
    private ZonedDateTime dateTime;

    public Long getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(Long seasonId) {
        this.seasonId = seasonId;
    }

    public final Long getGroupTypeId() {
        return groupTypeId;
    }

    public final void setGroupTypeId(Long groupTypeId) {
        this.groupTypeId = groupTypeId;
    }

    public final ZonedDateTime getDateTime() {
        return dateTime;
    }

    public final void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

}
