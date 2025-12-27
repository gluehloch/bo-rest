/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2013-2022 by Andre Winkler. All rights
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
import java.util.stream.Collectors;

import de.betoffice.storage.tip.GameTipp;
import de.betoffice.web.json.GameResultJson;
import de.betoffice.web.json.GameTippJson;

/**
 * Map a {@link GameTipp} to {@link GameTippJson}.
 * 
 * @author Andre Winkler
 */
public class GameTippJsonMapper {

    public static GameTippJson map(GameTipp tipp, GameTippJson gameTippJson) {
        gameTippJson.setNickname(tipp.getUser().getNickname().value());
        gameTippJson.setTipp(GameResultJsonMapper.map(tipp.getTipp(), new GameResultJson()));
        gameTippJson.setPoints(tipp.getPoints());
        return gameTippJson;
    }

    public static List<GameTippJson> map(List<GameTipp> gameTipp) {
        return gameTipp.stream().map((tipp) -> {
            GameTippJson json = new GameTippJson();
            json = map(tipp, json);
            return json;
        }).collect(Collectors.toList());
    }

}
