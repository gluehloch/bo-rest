package de.betoffice.web.json;

import java.time.ZonedDateTime;

import de.betoffice.storage.OpenligaObject;
import de.betoffice.storage.group.GroupTypeDto;
import de.betoffice.storage.season.GameResultDto;
import de.betoffice.storage.team.TeamDto;

public interface IGameJson extends OpenligaObject {
    void setId(Long id);

    Long getId();

    int getIndex();

    void setIndex(int index);

    Long getRoundId();

    void setRoundId(Long roundId);

    ZonedDateTime getDateTime();

    void setDateTime(ZonedDateTime dateTime);

    TeamDto getHomeTeam();

    void setHomeTeam(TeamDto homeTeam);

    TeamDto getGuestTeam();

    void setGroupType(GroupTypeDto groupType);

    GroupTypeDto getGroupType();

    void setGuestTeam(TeamDto guestTeam);

    GameResultDto getHalfTimeResult();

    void setHalfTimeResult(GameResultDto halfTimeResult);

    GameResultDto getResult();

    void setResult(GameResultDto result);

    GameResultDto getOvertimeResult();

    void setOvertimeResult(GameResultDto overtimeResult);

    GameResultDto getPenaltyResult();

    void setPenaltyResult(GameResultDto penaltyResult);

    boolean isFinished();

    void setFinished(boolean finished);

    boolean isKo();

    void setKo(boolean ko);

}
