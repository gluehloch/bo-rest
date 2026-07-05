package de.betoffice.web.admin;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.betoffice.service.MasterDataManagerService;
import de.betoffice.service.SeasonManagerService;
import de.betoffice.storage.group.entity.GroupType;
import de.betoffice.storage.season.entity.GameList;
import de.betoffice.storage.season.entity.Group;
import de.betoffice.storage.season.entity.Season;
import de.betoffice.validation.ValidationMessage;
import de.betoffice.validation.ValidationMessages;
import de.betoffice.web.json.round.AddRoundJson;
import de.betoffice.web.json.round.UpdateRoundJson;

@Component
public class RoundHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RoundHandler.class);

    private final MasterDataManagerService masterDataManagerService;
    private final SeasonManagerService seasonManagerService;

    public RoundHandler(MasterDataManagerService masterDataManagerService, SeasonManagerService seasonManagerService) {
        this.masterDataManagerService = masterDataManagerService;
        this.seasonManagerService = seasonManagerService;
    }

    public ValidationMessages addRound(long seasonId, AddRoundJson round) {
        if (seasonId != round.getSeasonId()) {
            LOG.error("Seaosn id from path variable {} does not match season id from request body {}.",
                    seasonId,
                    round.getSeasonId());
            return ValidationMessages.of(ValidationMessage.error(ValidationMessage.MessageType.SEASON_ID_MISMATCH,
                    seasonId,
                    round.getSeasonId()));
        }

        final Season season = seasonManagerService.findSeasonById(seasonId);
        final GroupType groupType = masterDataManagerService.findGroupType(round.getGroupTypeId());
        final Group group = seasonManagerService.findGroup(season, groupType);

        if (group == null) {
            LOG.error("Can´t find group with groupType={} for season with id={}.", groupType, seasonId);
            return ValidationMessages.of(
                    ValidationMessage.error(ValidationMessage.MessageType.GROUP_TYPE_NOT_FOUND,
                            season.getReference().getName(),
                            season.getReference().getYear(),
                            groupType));
        }

        seasonManagerService.addRound(season, round.getDateTime(), group.getGroupType());
        return ValidationMessages.ok();
    }

    public ValidationMessages updateRound(long seasonId, long roundId, UpdateRoundJson round) {
        if (seasonId != round.getSeasonId()) {
            LOG.error("Seaosn id from path variable {} does not match season id from request body {}.",
                    seasonId,
                    round.getSeasonId());
            return ValidationMessages.of(ValidationMessage.error(ValidationMessage.MessageType.SEASON_ID_MISMATCH,
                    seasonId,
                    round.getSeasonId()));
        }

        if (roundId != round.getRoundId()) {
            LOG.error("Round id from path variable {} does not match round id from request body {}.",
                    roundId,
                    round.getRoundId());
            return ValidationMessages.of(ValidationMessage.error(ValidationMessage.MessageType.ROUND_ID_MISMATCH,
                    roundId,
                    round.getRoundId()));
        }

        final Optional<GameList> roundEntity = seasonManagerService.findRoundGames(round.getRoundId());
        if (roundEntity.isEmpty()) {
            LOG.error("Can´t find round with id={}.", round.getRoundId());
            return ValidationMessages.of(
                    ValidationMessage.error(ValidationMessage.MessageType.ROUND_ID_NOT_FOUND,
                            round.getRoundId()));
        }

        final Season season = seasonManagerService.findSeasonById(seasonId);
        final GroupType groupType = masterDataManagerService.findGroupType(round.getGroupTypeId());
        final Group group = seasonManagerService.findGroup(season, groupType);

        if (group == null) {
            LOG.error("Can´t find group with groupType={} for season with id={}.", groupType, seasonId);
            return ValidationMessages.of(
                    ValidationMessage.error(ValidationMessage.MessageType.GROUP_TYPE_NOT_FOUND,
                            season.getReference().getName(),
                            season.getReference().getYear(),
                            groupType));
        }

        seasonManagerService.updateRound(season, roundEntity.get().getIndex(), round.getDateTime(), groupType);
        return ValidationMessages.ok();
    }

}
