/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2013-2017 by Andre Winkler. All
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

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import de.betoffice.storage.season.entity.GameEntity;
import de.betoffice.storage.season.entity.GameListEntity;
import de.betoffice.storage.season.entity.SeasonEntity;
import de.betoffice.storage.tip.GameTippEntity;
import de.betoffice.web.json.round.RoundJson;

/**
 * Assembles JSON objects with other JSON objects.
 * 
 * @author Andre Winkler
 */
public class JsonAssembler {

    public static class SeasonAssembler {
        private SeasonEntity season;
        private List<GameListEntity> rounds;
        private GameListEntity currentRound;

        private SeasonAssembler(SeasonEntity _season) {
            season = _season;
        }

        public SeasonAssembler rounds() {
            rounds = season.toGameList();
            return this;
        }

        public SeasonAssembler rounds(List<GameListEntity> _rounds) {
            rounds = _rounds;
            return this;
        }

        public SeasonAssembler rounds(Predicate<GameListEntity> filter) {
            rounds = season.toGameList(filter);
            return this;
        }

        public SeasonAssembler currentRound(GameListEntity _currentRound) {
            currentRound = _currentRound;
            return this;
        }

        public SeasonAssembler currentRound(Optional<GameListEntity> _currentRound) {
            return currentRound(_currentRound.orElse(null));
        }

        public SeasonJson assemble() {
            SeasonJson seasonJson = JsonBuilder.toJson(season);
            if (rounds == null || rounds.isEmpty()) {
                seasonJson.getRounds().clear();
            } else {
                List<RoundJson> gameListJson = JsonBuilder
                        .toJsonWithGameList(rounds);
                seasonJson.getRounds().clear();
                seasonJson.getRounds().addAll(gameListJson);
            }

            if (currentRound != null) {
                seasonJson.setCurrentRoundId(currentRound.getId());
            }

            return seasonJson;
        }
    }

    public static class RoundAssembler {
        private GameListEntity round;
        private List<GameEntity> games;
        private List<GameTippEntity> tipps;
        private boolean lastRound = false;

        private boolean hasToAddTipp = false;
        private boolean hasToAddEmptyTipp = false;

        private RoundAssembler(GameListEntity _round) {
            round = _round;
        }

        public RoundAssembler games(Predicate<GameEntity> filter) {
            games = round.toList(filter);
            return this;
        }

        public RoundAssembler games(List<GameEntity> _games) {
            games = _games;
            return this;
        }

        public RoundAssembler games() {
            games = round.unmodifiableList();
            return this;
        }

        public RoundAssembler tipps() {
            hasToAddTipp = true;
            return this;
        }

        public RoundAssembler tipps(List<GameTippEntity> _tipps) {
            tipps = _tipps;
            return this;
        }

        public RoundAssembler emptyTipp() {
            hasToAddEmptyTipp = true;
            return this;
        }

        public RoundAssembler lastRound(boolean _lastRound) {
            lastRound = _lastRound;
            return this;
        }

        public RoundJson assemble() {
            RoundJson roundJson = JsonBuilder.toJson(round);
            if (games == null || games.isEmpty()) {
                roundJson.getGames().clear();
            } else {
                List<GameJson> gameJsons = null;
                if (hasToAddEmptyTipp) {
                    gameJsons = JsonBuilder.toJsonWithGames(games);
                    for (GameJson gj : gameJsons) {
                        GameTippJson gameTippJson = new GameTippJson();
                        gameTippJson.setTipp(new GameResultJson());
                        gj.addTipp(gameTippJson);
                    }
                } else if (tipps != null && !tipps.isEmpty()) {
                    gameJsons = JsonBuilder.toJsonWithGamesAndTipps(games, tipps);
                } else if (hasToAddTipp) {
                    gameJsons = JsonBuilder.toJsonWithGamesAndTipps(games, tipps);
                } else {
                    gameJsons = JsonBuilder.toJsonWithGames(games);
                }

                roundJson.getGames().clear();
                roundJson.getGames().addAll(gameJsons);
            }

            roundJson.setLastRound(lastRound);
            roundJson.setTippable(!isFinished(roundJson));

            return roundJson;
        }

        private boolean isFinished(RoundJson round) {
            boolean finished = true;
            if (games == null || games.isEmpty()) {
                // No games? Finished or what? I guess, it is finished.
                finished = false;
            } else {
                for (IGameJson game : round.getGames()) {
                    finished = finished && game.isFinished();
                }
            }
            return finished;
        }
    }

    public SeasonAssembler build(SeasonEntity season) {
        return new SeasonAssembler(season);
    }

    public RoundAssembler build(GameListEntity round) {
        return new RoundAssembler(round);
    }

}
