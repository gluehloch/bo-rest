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
import java.util.function.Predicate;

import de.betoffice.storage.season.GameDto;
import de.betoffice.storage.season.GameResultDto;
import de.betoffice.storage.season.RoundDto;
import de.betoffice.storage.season.DtoAssembler.SeasonAssembler;
import de.betoffice.storage.season.entity.GameEntity;
import de.betoffice.storage.season.entity.GameListEntity;
import de.betoffice.storage.season.entity.SeasonEntity;
import de.betoffice.storage.tip.GameTippEntity;

/**
 * Assembles JSON objects with other JSON objects.
 * 
 * @author Andre Winkler
 */
public class JsonAssembler {

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

        public RoundDto assemble() {
            RoundDto roundJson = JsonBuilder.toJson(round);
            if (games == null || games.isEmpty()) {
                roundJson.getGames().clear();
            } else {
                List<GameDto> gameJsons = null;
                if (hasToAddEmptyTipp) {
                    gameJsons = JsonBuilder.toJsonWithGames(games);
                    for (GameDto gj : gameJsons) {
                        GameTippJson gameTippJson = new GameTippJson();
                        gameTippJson.setTipp(new GameResultDto());
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

        private boolean isFinished(RoundDto round) {
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
