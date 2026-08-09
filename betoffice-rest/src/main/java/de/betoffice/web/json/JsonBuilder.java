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
import de.betoffice.storage.group.entity.GroupTypeEntity;
import de.betoffice.storage.season.entity.GameEntity;
import de.betoffice.storage.season.entity.GameListEntity;
import de.betoffice.storage.season.entity.GameResult;
import de.betoffice.storage.season.entity.SeasonEntity;
import de.betoffice.storage.team.TeamResult;
import de.betoffice.storage.team.entity.TeamEntity;
import de.betoffice.storage.tip.GameTippEntity;
import de.betoffice.storage.user.UserResult;
import de.betoffice.web.json.builder.GameJsonMapper;
import de.betoffice.web.json.builder.GameResultJsonMapper;
import de.betoffice.web.json.builder.GameTippJsonMapper;
import de.betoffice.web.json.builder.GroupTypeJsonMapper;
import de.betoffice.web.json.builder.RoundJsonMapper;
import de.betoffice.web.json.builder.SeasonJsonMapper;
import de.betoffice.web.json.builder.SecurityTokenJsonMapper;
import de.betoffice.web.json.builder.TeamJsonMapper;
import de.betoffice.web.json.builder.TeamResultJsonMapper;
import de.betoffice.web.json.builder.UserJsonMapper;
import de.betoffice.web.json.round.RoundJson;

/**
 * Create JSON objects.
 *
 * @author Andre Winkler
 */
public class JsonBuilder {

    public static SecurityTokenJson toJson(SecurityToken securityToken) {
        return SecurityTokenJsonMapper.map(securityToken, new SecurityTokenJson());
    }

    public static SeasonJson toJson(SeasonEntity  season) {
        return SeasonJsonMapper.map(season, new SeasonJson());
    }

    public static List<SeasonJson> toJsonWithSeasons(List<SeasonEntity> seasons) {
        return SeasonJsonMapper.map(seasons);
    }

    public static RoundJson toJson(GameListEntity gameList) {
        return RoundJsonMapper.map(gameList, new RoundJson());
    }

    public static List<RoundJson> toJsonWithGameList(List<GameListEntity> rounds) {
        return RoundJsonMapper.map(rounds);
    }

    public static RoundJson toJsonWithGames(GameListEntity gameList) {
        RoundJson roundJson = JsonBuilder.toJson(gameList);
        List<GameJson> gameJson = JsonBuilder.toJsonWithGames(gameList.unmodifiableList());
        roundJson.getGames().addAll(gameJson);
        return roundJson;
    }

    public static GroupTypeJson toJson(GroupTypeEntity groupType) {
        return GroupTypeJsonMapper.map(groupType, new GroupTypeJson());
    }

    public static List<GroupTypeJson> toJsonWithGroupTypes(List<GroupTypeEntity> groupTypes) {
        return GroupTypeJsonMapper.map(groupTypes);
    }

    public static TeamResultJson toJson(TeamResult teamResult) {
        return TeamResultJsonMapper.map(teamResult, new TeamResultJson());
    }

    public static TeamJson toJson(TeamEntity team) {
        return TeamJsonMapper.map(team, new TeamJson());
    }

    public static List<TeamJson> toJsonWithTeams(List<TeamEntity> teams) {
        return TeamJsonMapper.map(teams);
    }

    public static UserJson toJson(UserResult userResult) {
        return UserJsonMapper.map(userResult, new UserJson());
    }

    public static GameResultJson toJson(GameResult gameResult) {
        return GameResultJsonMapper.map(gameResult, new GameResultJson());
    }

    public static GameTippJson toJson(GameTippEntity tipp) {
        return GameTippJsonMapper.map(tipp, new GameTippJson());
    }

    public static GameJson toJson(GameEntity game) {
        GameJson gameJson = GameJsonMapper.map(game, new GameJson());
        return gameJson;
    }

    public static GameWithGoalsJson toGameWithGoalsJson(GameEntity game) {
        GameWithGoalsJson gameJson = GameJsonMapper.map(game, new GameWithGoalsJson());
        return gameJson;
    }

    public static List<GameJson> toJsonWithGames(List<GameEntity> games) {
        List<GameJson> gameJsons = new ArrayList<>();
        for (GameEntity game : games) {
            gameJsons.add(JsonBuilder.toJson(game));
        }
        return gameJsons;
    }

    public static List<GameJson> toJsonWithGamesAndTipps(List<GameEntity> games, List<GameTippEntity> tipps) {
        List<GameJson> gameJsons = games.stream().map(game -> JsonBuilder.toJson(game)).collect(Collectors.toList());

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

    public static List<GameJson> toJsonWithGamesAndTipps(List<GameEntity> games, Set<GameTippEntity> tipps) {
        List<GameJson> gameJsons = new ArrayList<>();
        for (GameEntity game : games) {
            GameJson gameJson = JsonBuilder.toJson(game);
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
