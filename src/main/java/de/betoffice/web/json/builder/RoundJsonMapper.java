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

import de.betoffice.web.json.RoundJson;
import de.winkler.betoffice.storage.GameList;

/**
 * A mapper for {@link GameList} to {@link RoundJson}.
 * 
 * @author Andre Winkler
 */
public class RoundJsonMapper {

    public RoundJson map(GameList round, RoundJson roundJson) {
        roundJson.setId(round.getId());
        roundJson.setDateTime(round.getDateTime());
        roundJson.setIndex(round.getIndex() + 1);
        roundJson.setSeasonId(round.getSeason().getId());
        roundJson.setSeasonName(round.getSeason().getReference().getName());
        roundJson.setSeasonYear(round.getSeason().getReference().getYear());
        return roundJson;
    }

    public List<RoundJson> map(List<GameList> rounds) {
        return rounds.stream().map((round) -> {
            RoundJson json = new RoundJson();
            json = map(round, json);
            return json;
        }).collect(Collectors.toList());
    }

}
