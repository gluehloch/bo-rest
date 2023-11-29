/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2013-2023 by Andre Winkler. All rights
 * reserved.
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

package de.betoffice.web.json.builder;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import de.betoffice.web.JsonBuilder;
import de.betoffice.web.json.GameJson;
import de.betoffice.web.json.GameResultJson;
import de.betoffice.web.json.IGameJson;
import de.winkler.betoffice.storage.Game;

/**
 * Mapping of {@link Game} to {@link GameJson}.
 * 
 * @author Andre Winkler
 */
public class GameJsonMapper {

	public <T extends IGameJson> T map(Game game, T gameJson) {
		gameJson.setId(game.getId());
		gameJson.setRoundId(game.getGameList().getId());
		gameJson.setOpenligaid(game.getOpenligaid());
		gameJson.setIndex(game.getIndex());
		gameJson.setFinished(game.isPlayed());
		gameJson.setKo(game.isKo());
		gameJson.setDateTime(game.getDateTime());

		GameResultJson halfTimeGoals = JsonBuilder.toJson(game.getHalfTimeGoals());
		gameJson.setHalfTimeResult(halfTimeGoals);

		GameResultJson gameResult = JsonBuilder.toJson(game.getResult());
		gameJson.setResult(gameResult);

		GameResultJson penaltyGoals = JsonBuilder.toJson(game.getPenaltyGoals());
		gameJson.setPenaltyResult(penaltyGoals);

		GameResultJson overtimeGoals = JsonBuilder.toJson(game.getOverTimeGoals());
		gameJson.setOvertimeResult(overtimeGoals);

		gameJson.setHomeTeam(JsonBuilder.toJson(game.getHomeTeam()));
		gameJson.setGuestTeam(JsonBuilder.toJson(game.getGuestTeam()));

		return gameJson;
	}

	public <T extends IGameJson> List<T> map(List<Game> games, Supplier<T> supplier) {
		return games.stream().map((game) -> {
			T json = supplier.get();
			json = map(game, json);
			return json;
		}).collect(Collectors.toList());
	}

}
