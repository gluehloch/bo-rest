/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2013-2024 by Andre Winkler. All
 * rights reserved.
 * ============================================================================
 * GNU GENERAL PUBLIC LICENSE TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND
 * MODIFICATION
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package de.betoffice.web.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.betoffice.service.SecurityToken;
import de.betoffice.storage.group.GroupTypeDto;
import de.betoffice.storage.group.entity.GroupTypeEntity;
import de.betoffice.storage.season.GameDto;
import de.betoffice.storage.season.GameResultDto;
import de.betoffice.storage.season.RoundDto;
import de.betoffice.storage.season.SeasonDto;
import de.betoffice.storage.season.entity.GameEntity;
import de.betoffice.storage.season.entity.GameListEntity;
import de.betoffice.storage.season.entity.GameResult;
import de.betoffice.storage.season.entity.SeasonEntity;
import de.betoffice.storage.season.entity.SeasonDtoMapper;
import de.betoffice.storage.team.TeamDto;
import de.betoffice.storage.team.TeamResult;
import de.betoffice.storage.team.entity.TeamEntity;
import de.betoffice.storage.team.entity.TeamDtoMapper;
import de.betoffice.storage.tip.GameTippEntity;
import de.betoffice.storage.user.UserResult;
import de.betoffice.web.json.builder.GameJsonMapper;
import de.betoffice.web.json.builder.GameResultJsonMapper;
import de.betoffice.web.json.builder.GameTippJsonMapper;
import de.betoffice.web.json.builder.GroupTypeJsonMapper;
import de.betoffice.web.json.builder.RoundJsonMapper;
import de.betoffice.web.json.builder.SecurityTokenJsonMapper;
import de.betoffice.web.json.builder.TeamResultJsonMapper;
import de.betoffice.web.json.builder.UserJsonMapper;

/**
 * Create JSON objects.
 *
 * @author Andre Winkler
 */
public class JsonBuilder {

    public static SecurityTokenJson toJson(SecurityToken securityToken) {
        return SecurityTokenJsonMapper.map(securityToken, new SecurityTokenJson());
    }

    public static SeasonDto toJson(SeasonEntity  season) {
        return SeasonDtoMapper.map(season, new SeasonDto());
    }

    public static List<SeasonDto> toJsonWithSeasons(List<SeasonEntity> seasons) {
        return SeasonDtoMapper.map(seasons);
    }

    public static RoundDto toJson(GameListEntity gameList) {
        return RoundJsonMapper.map(gameList, new RoundDto());
    }

    public static List<RoundDto> toJsonWithGameList(List<GameListEntity> rounds) {
        return RoundJsonMapper.map(rounds);
    }

    public static RoundDto toJsonWithGames(GameListEntity gameList) {
        RoundDto roundJson = JsonBuilder.toJson(gameList);
        List<GameDto> gameJson = JsonBuilder.toJsonWithGames(gameList.unmodifiableList());
        roundJson.getGames().addAll(gameJson);
        return roundJson;
    }

    public static GroupTypeDto toJson(GroupTypeEntity groupType) {
        return GroupTypeJsonMapper.map(groupType, new GroupTypeDto());
    }

    public static List<GroupTypeDto> toJsonWithGroupTypes(List<GroupTypeEntity> groupTypes) {
        return GroupTypeJsonMapper.map(groupTypes);
    }

    public static TeamResultJson toJson(TeamResult teamResult) {
        return TeamResultJsonMapper.map(teamResult, new TeamResultJson());
    }

    public static TeamDto toJson(TeamEntity team) {
        return TeamDtoMapper.map(team, new TeamDto());
    }

    public static List<TeamDto> toJsonWithTeams(List<TeamEntity> teams) {
        return TeamDtoMapper.map(teams);
    }

    public static UserJson toJson(UserResult userResult) {
        return UserJsonMapper.map(userResult, new UserJson());
    }

    public static GameResultDto toJson(GameResult gameResult) {
        return GameResultJsonMapper.map(gameResult, new GameResultDto());
    }

    public static GameTippJson toJson(GameTippEntity tipp) {
        return GameTippJsonMapper.map(tipp, new GameTippJson());
    }

    public static GameDto toJson(GameEntity game) {
        GameDto gameJson = GameJsonMapper.map(game, new GameDto());
        return gameJson;
    }

    public static GameWithGoalsJson toGameWithGoalsJson(GameEntity game) {
        GameWithGoalsJson gameJson = GameJsonMapper.map(game, new GameWithGoalsJson());
        return gameJson;
    }

    public static List<GameDto> toJsonWithGames(List<GameEntity> games) {
        List<GameDto> gameJsons = new ArrayList<>();
        for (GameEntity game : games) {
            gameJsons.add(JsonBuilder.toJson(game));
        }
        return gameJsons;
    }

    public static List<GameDto> toJsonWithGamesAndTipps(List<GameEntity> games, List<GameTippEntity> tipps) {
        List<GameDto> gameJsons = games.stream().map(game -> JsonBuilder.toJson(game)).collect(Collectors.toList());

        gameJsons.stream().forEach(gameJson -> {
            tipps.stream().filter(t -> {
                if (t.getGame().getId() != null && gameJson.getId() != null) {
                    return t.getGame().getId().equals(gameJson.getId());
                } else {
                    return false;
                }
            }).forEach((tipp) -> {
                GameTippJson tippJson = toJson(tipp);
                gameJson.addTipp(tippJson);
            });
        });

        return gameJsons;
    }

    public static List<GameDto> toJsonWithGamesAndTipps(List<GameEntity> games, Set<GameTippEntity> tipps) {
        List<GameDto> gameJsons = new ArrayList<>();
        for (GameEntity game : games) {
            GameDto gameJson = JsonBuilder.toJson(game);
            gameJsons.add(gameJson);
            for (GameTippEntity tipp : tipps) {
                GameTippJson tippJson = toJson(tipp);
                gameJson.addTipp(tippJson);
            }
        }
        return gameJsons;
    }

    public static List<GameTippJson> toJsonWithGameTipp(List<GameTippEntity> tipps) {
        List<GameTippJson> gameJsons = new ArrayList<>();
        for (GameTippEntity tipp : tipps) {
            GameTippJson gameTippJson = toJson(tipp);
            gameJsons.add(gameTippJson);
        }
        return gameJsons;
    }

}
