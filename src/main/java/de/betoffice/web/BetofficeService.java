/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2000-2021 by Andre Winkler. All
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

package de.betoffice.web;

import java.util.List;
import java.util.Optional;

import de.betoffice.web.json.DetailGameJson;
import de.betoffice.web.json.GameJson;
import de.betoffice.web.json.GroupTypeJson;
import de.betoffice.web.json.PingJson;
import de.betoffice.web.json.RoundAndTableJson;
import de.betoffice.web.json.RoundJson;
import de.betoffice.web.json.SeasonJson;
import de.betoffice.web.json.TeamJson;
import de.betoffice.web.json.UserTableJson;

/**
 * Betoffice JSON service interface
 *
 * @author Andre Winkler
 */
public interface BetofficeService {

	/**
	 * Find a season by id
	 *
	 * @param seasonId the season id
	 * @return the season
	 */
	SeasonJson findSeasonById(Long seasonId);

	/**
	 * Find all groups of a season.
	 *
	 * @param seasonId the season id
	 * @return the group types of a season
	 */
	List<GroupTypeJson> findAllGroups(Long seasonId);

	/**
     * Find all rounds of a season
     *
     * @param seasonId    the season id
     * @return the rounds of a season
     */
    List<RoundJson> findAllRounds(Long seasonId);

	/**
	 * Find all rounds of a season
	 *
	 * @param seasonId    the season id
	 * @param groupTypeId the group type id
	 * @return the rounds of a season
	 */
	SeasonJson findAllRounds(Long seasonId, Long groupTypeId);
	
	/**
	 * Find a round by id
	 *
	 * @param roundId the round id
	 * @return the round
	 */
	RoundJson findRound(Long roundId);

	/**
	 * Find a game by id
	 * 
	 * @param gameId the game id
	 * @return the game
	 */
	GameJson findGame(Long gameId);

	/**
	 * Find a game by id
	 * 
	 * @param gameId the game id
	 * @return the game
	 */
	DetailGameJson findDetailGame(Long gameId);

	/**
	 * Find a round by id. Returns only the games of a round with defined groupType.
	 *
	 * @param roundId     the round id
	 * @param groupTypeId the group type id
	 * @return the round
	 */
	RoundJson findRound(Long roundId, Long groupTypeId);

	/**
	 * Find the next round
	 *
	 * @param roundId the round id
	 * @return the next round from id
	 */
	RoundJson findNextRound(Long roundId);

	/**
	 * Find the prev round
	 *
	 * @param roundId the round id
	 * @return the prev round from id
	 */
	RoundJson findPrevRound(Long roundId);

	/**
	 * Find a round by id
	 *
	 * @param roundId     the round id
	 * @param groupTypeId the group type id
	 * @return the round
	 */
	RoundAndTableJson findRoundTable(Long roundId, Long groupTypeId);

	/**
	 * Find the next round
	 *
	 * @param roundId the round id
	 * @return the next round from id
	 */
	RoundAndTableJson findNextRoundTable(Long roundId);

	/**
	 * Find the prev round
	 *
	 * @param roundId the round id
	 * @return the prev round from id
	 */
	RoundAndTableJson findPrevRoundTable(Long roundId);

	/**
	 * Find current match round for a season
	 * 
	 * @param seasonId the season id
	 * @return the current round
	 */
	Optional<RoundJson> findCurrent(Long seasonId);

	/**
	 * Calculate the user ranking for the season.
	 * 
	 * @param seasonId the season ud
	 * @return user ranking
	 */
	UserTableJson calcUserRanking(Long seasonId);

	/**
	 * Calculate the user ranking for a specific round.
	 * 
	 * @param roundId the round id
	 * @return user ranking for a specific round
	 */
	UserTableJson calcUserRankingByRoundOnly(Long roundId);

	/**
	 * Calculate the user ranking till specified round.
	 * 
	 * @param roundId the round id
	 * @return user ranking
	 */
	UserTableJson calcUserRankingByRound(Long roundId);

	/**
	 * Calculate the user ranking till next specified round
	 * 
	 * @param roundId the round id
	 * @return user ranking
	 */
	UserTableJson calcUserRankingByNextRound(Long roundId);

	/**
	 * Calculate the user ranking till prev specified round
	 * 
	 * @param roundId the round id
	 * @return user ranking
	 */
	UserTableJson calcUserRankingByPrevRound(Long roundId);

	/**
	 * Find all teams
	 *
	 * @return all teams
	 */
	List<TeamJson> findAllTeams();

	/**
	 * Find all seasons
	 *
	 * @return all season
	 */
	List<SeasonJson> findAllSeason();

	/**
	 * Ping. Is the server alive?
	 * 
	 * @return Server local date and time.
	 */
	PingJson ping();

}