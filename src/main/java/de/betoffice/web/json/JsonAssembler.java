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

import de.winkler.betoffice.storage.Game;
import de.winkler.betoffice.storage.GameList;
import de.winkler.betoffice.storage.GameTipp;
import de.winkler.betoffice.storage.Season;

/**
 * Assembles JSON objects with other JSON objects.
 * 
 * @author Andre Winkler
 */
public class JsonAssembler {

    public static class SeasonAssembler {
        private Season season;
        private List<GameList> rounds;
        private GameList currentRound;

        private SeasonAssembler(Season _season) {
            season = _season;
        }

        public SeasonAssembler rounds() {
            rounds = season.toGameList();
            return this;
        }

        public SeasonAssembler rounds(List<GameList> _rounds) {
            rounds = _rounds;
            return this;
        }

        public SeasonAssembler rounds(Predicate<GameList> filter) {
            rounds = season.toGameList(filter);
            return this;
        }
        
        public SeasonAssembler currentRound(GameList _currentRound) {
            currentRound = _currentRound;
            return this;
        }
        
        public SeasonAssembler currentRound(Optional<GameList> _currentRound) {
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
        private GameList round;
        private List<Game> games;
        private List<GameTipp> tipps;
        private boolean lastRound = false;

        private boolean hasToAddTipp = false;
        private boolean hasToAddEmptyTipp = false;

        private RoundAssembler(GameList _round) {
            round = _round;
        }

        public RoundAssembler games(Predicate<Game> filter) {
            games = round.toList(filter);
            return this;
        }

        public RoundAssembler games(List<Game> _games) {
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

        public RoundAssembler tipps(List<GameTipp> _tipps) {
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

    public SeasonAssembler build(Season season) {
        return new SeasonAssembler(season);
    }

    public RoundAssembler build(GameList round) {
        return new RoundAssembler(round);
    }

}
