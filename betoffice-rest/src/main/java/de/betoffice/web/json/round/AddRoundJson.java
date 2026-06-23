package de.betoffice.web.json.round;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import de.betoffice.storage.group.GroupTypeEnum;
import de.betoffice.web.json.JsonDateTimeFormat;

public class AddRoundJson {

    private Long seasonId;
    private GroupTypeEnum groupType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JsonDateTimeFormat.DATETIME_PATTERN, timezone = JsonDateTimeFormat.TIMZONE)
    private ZonedDateTime dateTime;

    public Long getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(Long seasonId) {
        this.seasonId = seasonId;
    }

    public final GroupTypeEnum getGroupType() {
        return groupType;
    }

    public final void setGroupType(GroupTypeEnum groupType) {
        this.groupType = groupType;
    }

    public final ZonedDateTime getDateTime() {
        return dateTime;
    }

    public final void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

}
