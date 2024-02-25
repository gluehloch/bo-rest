/*
 * ============================================================================
 * Project betoffice-jweb Copyright (c) 2015-2024 by Andre Winkler. All rights
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

package de.betoffice.web;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.betoffice.web.json.DetailGameJson;
import de.betoffice.web.json.GameJson;
import de.betoffice.web.json.GroupTypeJson;
import de.betoffice.web.json.PingJson;
import de.betoffice.web.json.RoundAndTableJson;
import de.betoffice.web.json.RoundJson;
import de.betoffice.web.json.SeasonJson;
import de.betoffice.web.json.UserTableJson;

@CrossOrigin
@RestController
@RequestMapping("/office")
public class SeasonController {

    // ------------------------------------------------------------------------
    // The beans
    // ------------------------------------------------------------------------

    private final BetofficeService betofficeBasicJsonService;

    @Autowired
    public SeasonController(BetofficeService betofficeService) {
        this.betofficeBasicJsonService = betofficeService;
    }

    // ------------------------------------------------------------------------

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public PingJson ping(HttpServletRequest request) {
        return betofficeBasicJsonService.ping();
    }

    // ------------------------------------------------------------------------

    @RequestMapping(value = "/season", method = RequestMethod.GET)
    public List<SeasonJson> findAllSeason(HttpServletResponse response) {
        return betofficeBasicJsonService.findAllSeason();
    }

    @RequestMapping(value = "/season/{seasonId}", method = RequestMethod.GET)
    public SeasonJson findSeasonById(@PathVariable("seasonId") Long seasonId, HttpServletResponse response) {
        return betofficeBasicJsonService.findSeasonById(seasonId);
    }

    @RequestMapping(value = "/season/{seasonId}/group", method = RequestMethod.GET)
    public List<GroupTypeJson> findGroupTypes(@PathVariable("seasonId") Long seasonId) {
        return betofficeBasicJsonService.findAllGroups(seasonId);
    }

    @RequestMapping(value = "/season/{seasonId}/group/{groupTypeId}/round/all", method = RequestMethod.GET)
    public SeasonJson findAllRounds(@PathVariable("seasonId") Long seasonId,
            @PathVariable("groupTypeId") Long groupTypeId) {
        return betofficeBasicJsonService.findAllRounds(seasonId, groupTypeId);
    }

    //
    // round
    //

    /**
     * Return the current (next to play and/or to tipp) round of a season.
     *
     * @param  seasonId Season ID
     * @return          The current round of a season
     */
    @RequestMapping(value = "/season/{seasonId}/current", method = RequestMethod.GET)
    public RoundJson findNextTipp(@PathVariable("seasonId") Long seasonId) {
        return betofficeBasicJsonService.findTippRound(seasonId).orElse(null);
    }

    @RequestMapping(value = "/season/{seasonId}/round", method = RequestMethod.GET)
    public List<RoundJson> findRounds(@PathVariable("seasonId") Long seasonId) {
        return betofficeBasicJsonService.findAllRounds(seasonId);
    }

    @RequestMapping(value = "/season/round/{roundId}", method = RequestMethod.GET)
    public RoundJson findRound(@PathVariable("roundId") Long roundId) {
        return betofficeBasicJsonService.findRound(roundId);
    }

    @RequestMapping(value = "/season/round/{roundId}/next", method = RequestMethod.GET)
    public RoundJson findNextRound(@PathVariable("roundId") Long roundId) {
        return betofficeBasicJsonService.findNextRound(roundId);
    }

    @RequestMapping(value = "/season/round/{roundId}/prev", method = RequestMethod.GET)
    public RoundJson findPrevRound(@PathVariable("roundId") Long roundId) {
        return betofficeBasicJsonService.findPrevRound(roundId);
    }

    //
    // game
    //

    @RequestMapping(value = "/game/{gameId}", method = RequestMethod.GET)
    public GameJson findGame(@PathVariable("gameId") Long gameId) {
        return betofficeBasicJsonService.findGame(gameId);
    }

    @RequestMapping(value = "/game-details/{gameId}", method = RequestMethod.GET)
    public DetailGameJson findGameDetails(@PathVariable("gameId") Long gameId) {
    	return betofficeBasicJsonService.findDetailGame(gameId);
    }
    
    //
    // round and table
    //

    /*
     * @CrossOrigin
     * 
     * @RequestMapping(value = "/season/{seasonId}/roundtable/current", method =
     * RequestMethod.GET) public RoundAndTableJson
     * findCurrentRoundTable(@PathVariable("seasonId") Long seasonId) { RoundJson
     * currentSeason = betofficeBasicJsonService.findCurrent(seasonId); return null;
     * }
     */

    @RequestMapping(value = "/season/roundtable/{roundId}/group/{groupTypeId}", method = RequestMethod.GET)
    public RoundAndTableJson findRoundTable(@PathVariable("roundId") Long roundId,
            @PathVariable("groupTypeId") Long groupTypeId) {
        return betofficeBasicJsonService.findRoundTable(roundId, groupTypeId);
    }

    // TODO Implement and use me
    @RequestMapping(value = "/season/roundtable/{roundId}/next", method = RequestMethod.GET)
    public RoundAndTableJson findNextRoundTable(@PathVariable("roundId") Long roundId) {
        return betofficeBasicJsonService.findNextRoundTable(roundId);
    }

    // TODO Implement and use me
    @RequestMapping(value = "/season/roundtable/{roundId}/prev", method = RequestMethod.GET)
    public RoundAndTableJson findPrevRoundTable(@PathVariable("roundId") Long roundId) {
        return betofficeBasicJsonService.findPrevRoundTable(roundId);
    }

    //
    // user ranking
    //

    @RequestMapping(value = "/ranking/season/{seasonId}", method = RequestMethod.GET)
    public UserTableJson findUserTableBySeason(@PathVariable("seasonId") Long seasonId) {

        return betofficeBasicJsonService.calcUserRanking(seasonId);
    }

    @RequestMapping(value = "/ranking/roundonly/{roundId}", method = RequestMethod.GET)
    public UserTableJson findUserTableByRoundOnly(@PathVariable("roundId") Long roundId) {

        return betofficeBasicJsonService.calcUserRankingByRoundOnly(roundId);
    }

    @RequestMapping(value = "/ranking/round/{roundId}", method = RequestMethod.GET)
    public UserTableJson findUserTableByRound(@PathVariable("roundId") Long roundId) {

        return betofficeBasicJsonService.calcUserRankingByRound(roundId);
    }

    @RequestMapping(value = "/ranking/round/{roundId}/next", method = RequestMethod.GET)
    public UserTableJson findUserTableByNextRound(@PathVariable("roundId") Long roundId) {

        return betofficeBasicJsonService.calcUserRankingByNextRound(roundId);
    }

    @RequestMapping(value = "/ranking/round/{roundId}/prev", method = RequestMethod.GET)
    public UserTableJson findUserTableByPrevRound(@PathVariable("roundId") Long roundId) {

        return betofficeBasicJsonService.calcUserRankingByPrevRound(roundId);
    }

    /**
     * Convert a predefined exception to an HTTP Status code
     */
    @ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Access denied")
    @ExceptionHandler(AccessDeniedException.class)
    public void forbidden() {
    }

}
