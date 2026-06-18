package de.betoffice.web.json;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import de.betoffice.storage.group.GroupTypeEnum;

public class UpdateRoundJson extends AbstractIdentifier {

    private Long roundId;
    private GroupTypeEnum groupType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JsonDateTimeFormat.DATETIME_PATTERN, timezone = JsonDateTimeFormat.TIMZONE)
    private ZonedDateTime dateTime;

    public final Long getRoundId() {
        return roundId;
    }

    public final void setRoundId(Long roundId) {
        this.roundId = roundId;
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
