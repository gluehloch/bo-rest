package de.betoffice.web.json;

import java.time.ZonedDateTime;

public interface IGameJson extends OpenligaObject {
    void setId(Long id);

    Long getId();

    int getIndex();

    void setIndex(int index);

    Long getRoundId();

    void setRoundId(Long roundId);

    ZonedDateTime getDateTime();

    void setDateTime(ZonedDateTime dateTime);

    TeamJson getHomeTeam();

    void setHomeTeam(TeamJson homeTeam);

    TeamJson getGuestTeam();

    void setGroupType(GroupTypeJson groupType);

    GroupTypeJson getGroupType();

    void setGuestTeam(TeamJson guestTeam);

    GameResultJson getHalfTimeResult();

    void setHalfTimeResult(GameResultJson halfTimeResult);

    GameResultJson getResult();

    void setResult(GameResultJson result);

    GameResultJson getOvertimeResult();

    void setOvertimeResult(GameResultJson overtimeResult);

    GameResultJson getPenaltyResult();

    void setPenaltyResult(GameResultJson penaltyResult);

    boolean isFinished();

    void setFinished(boolean finished);

    boolean isKo();

    void setKo(boolean ko);

}
