package de.betoffice.web.admin;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.betoffice.service.MasterDataManagerService;
import de.betoffice.service.SeasonManagerService;
import de.betoffice.storage.group.entity.GroupTypeEntity;
import de.betoffice.storage.season.AddRoundJson;
import de.betoffice.storage.season.UpdateRoundDto;
import de.betoffice.storage.season.entity.GameListEntity;
import de.betoffice.storage.season.entity.GroupEntity;
import de.betoffice.storage.season.entity.SeasonEntity;
import de.betoffice.validation.ValidationMessage;
import de.betoffice.validation.ValidationMessages;

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

        final SeasonEntity season = seasonManagerService.findSeasonById(seasonId);
        if (season == null) {
            LOG.error("Can´t find season with id={}.", seasonId);
            return ValidationMessages.of(
                    ValidationMessage.error(ValidationMessage.MessageType.SEASON_ID_NOT_FOUND,
                            seasonId));
        }

        final GroupTypeEntity groupType = masterDataManagerService.findGroupType(round.getGroupTypeId());
        if (groupType == null) {
            LOG.error("Can´t find group type with id={}.", round.getGroupTypeId());
            return ValidationMessages.of(
                    ValidationMessage.error(ValidationMessage.MessageType.GROUPTYPE_ID_NOT_FOUND,
                            round.getGroupTypeId()));
        }

        final GroupEntity group = seasonManagerService.findGroup(season, groupType);
        if (group == null) {
            LOG.error("Can´t find group with groupType={} for season with id={}.", groupType, seasonId);
            return ValidationMessages.of(
                    ValidationMessage.error(ValidationMessage.MessageType.SEASON_GROUP_NOT_FOUND,
                            season.getReference().getName(),
                            season.getReference().getYear(),
                            groupType));
        }

        seasonManagerService.addRound(season, round.getDateTime(), group.getGroupType());
        return ValidationMessages.ok();
    }

    public ValidationMessages updateRound(long seasonId, long roundId, UpdateRoundDto round) {
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

        final Optional<GameListEntity> roundEntity = seasonManagerService.findRoundGames(round.getRoundId());
        if (roundEntity.isEmpty()) {
            LOG.error("Can´t find round with id={}.", round.getRoundId());
            return ValidationMessages.of(
                    ValidationMessage.error(ValidationMessage.MessageType.ROUND_ID_NOT_FOUND,
                            round.getRoundId()));
        }

        final SeasonEntity season = seasonManagerService.findSeasonById(seasonId);
        final GroupTypeEntity groupType = masterDataManagerService.findGroupType(round.getGroupTypeId());
        final GroupEntity group = seasonManagerService.findGroup(season, groupType);

        if (group == null) {
            LOG.error("Can´t find group with groupType={} for season with id={}.", groupType, seasonId);
            return ValidationMessages.of(
                    ValidationMessage.error(ValidationMessage.MessageType.SEASON_GROUP_NOT_FOUND,
                            season.getReference().getName(),
                            season.getReference().getYear(),
                            groupType));
        }

        seasonManagerService.updateRound(season, roundEntity.get().getIndex(), round.getDateTime(), groupType);
        return ValidationMessages.ok();
    }

    public ValidationMessages deleteRound(long seasonId, long roundId) {
        final SeasonEntity season = seasonManagerService.findSeasonById(seasonId);
        if (season == null) {
            LOG.error("Can´t find season with id={}.", seasonId);
            return ValidationMessages.of(
                    ValidationMessage.error(ValidationMessage.MessageType.SEASON_ID_NOT_FOUND,
                            seasonId));
        }

        final Optional<GameListEntity> roundEntity = seasonManagerService.findRoundGames(roundId);
        if (roundEntity.isEmpty()) {
            LOG.error("Can´t find round with id={}.", roundId);
            return ValidationMessages.of(
                    ValidationMessage.error(ValidationMessage.MessageType.ROUND_ID_NOT_FOUND,
                            roundId));
        }

        seasonManagerService.removeRound(season, roundEntity.get());
        return ValidationMessages.ok();
    }

}
