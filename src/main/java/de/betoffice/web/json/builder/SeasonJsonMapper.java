/*
 * ============================================================================
 * Project betoffice-jweb Copyright (c) 2022 by Andre Winkler. All rights
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

import de.betoffice.web.json.SeasonJson;
import de.winkler.betoffice.storage.Season;
import de.winkler.betoffice.storage.SeasonReference;
import de.winkler.betoffice.storage.enums.SeasonType;
import de.winkler.betoffice.storage.enums.TeamType;

/**
 * Mapping for {@link Season} to {@link SeasonJson}.
 * 
 * @author Andre Winkler
 */
public class SeasonJsonMapper {

    public SeasonJson map(Season season, SeasonJson seasonJson) {
        seasonJson.setId(season.getId());
        seasonJson.setName(season.getReference().getName());
        seasonJson.setYear(season.getReference().getYear());
        seasonJson.setSeasonType(season.getMode().toString());
        seasonJson.setTeamType(season.getTeamType().toString());

        if (season.getChampionshipConfiguration() != null) {
            seasonJson.setOpenligaLeagueSeason(
                    season.getChampionshipConfiguration()
                            .getOpenligaLeagueSeason());
            seasonJson.setOpenligaLeagueShortcut(season
                    .getChampionshipConfiguration()
                    .getOpenligaLeagueShortcut());
        }
        return seasonJson;
    }

    public List<SeasonJson> map(List<Season> seasons) {
        return seasons.stream().map((season) -> {
            SeasonJson json = new SeasonJson();
            json = map(season, json);
            return json;
        }).collect(Collectors.toList());
    }

    public Season reverse(SeasonJson seasonJson, Season season) {
        season.setMode(SeasonType.valueOf(seasonJson.getSeasonType()));
        season.setTeamType(TeamType.valueOf(seasonJson.getTeamType()));
        season.setReference(SeasonReference.of(seasonJson.getYear(), seasonJson.getName()));
        season.getChampionshipConfiguration().setOpenligaLeagueSeason(seasonJson.getOpenligaLeagueSeason());
        season.getChampionshipConfiguration().setOpenligaLeagueShortcut(seasonJson.getOpenligaLeagueShortcut());
        return season;
    }

}
