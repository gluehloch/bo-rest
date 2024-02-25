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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.betoffice.web.json.RoundJson;
import de.betoffice.web.json.SubmitTippRoundJson;
import de.betoffice.web.json.TeamJson;

@CrossOrigin
@RestController
@RequestMapping("/office")
public class TippController {

    private final BetofficeService betofficeBasicJsonService;
    private final OfficeTippService officeTippService;

    @Autowired
    public TippController(BetofficeService betofficeService, OfficeTippService officeTippService) {
        this.betofficeBasicJsonService = betofficeService;
        this.officeTippService = officeTippService;
    }

    /**
     * Returns the tipp of a user for a round.
     *
     * @param  roundId  Round ID
     * @param  nickName nick name
     * @return          The tipp of a user for a round
     */
    @RequestMapping(value = "/tipp/{roundId}/{nickName}", method = RequestMethod.GET)
    public RoundJson findTipp(
                @PathVariable("roundId") Long roundId,
                @PathVariable("nickName") String nickName) {
        return officeTippService.findTipp(roundId, nickName);
    }

    /**
     * The current round to tipp
     *
     * @param  seasonId Season ID
     * @param  nickName Der Name des Users
     * @return          The current tipp
     */
    @RequestMapping(value = "/tipp/{seasonId}/{nickname}/current", method = RequestMethod.GET)
    public RoundJson findCurrentTipp(
                @PathVariable("seasonId") Long seasonId,
                @PathVariable("nickname") String nickName) {
        return officeTippService.findCurrentTipp(seasonId, nickName).orElse(null);
    }

    /**
     * The next tipp ahead of <code>roundId</code>
     *
     * @param  roundId  Round ID
     * @param  nickName nick name
     * @return          The next tipp ahead of <code>roundId</code>
     */
    @RequestMapping(value = "/tipp/{roundId}/{nickName}/next", method = RequestMethod.GET)
    public RoundJson findNextTipp(
                @PathVariable("roundId") Long roundId,
                @PathVariable("nickName") String nickName) {
        return officeTippService.findNextTipp(roundId, nickName).orElse(null);
    }

    /**
     * The previous tipp behind of <code>roundId</code>
     *
     * @param  roundId  Round ID
     * @param  nickName nick name
     * @return          The previous tipp behind of <code>roundId</code>
     */
    @RequestMapping(value = "/tipp/{roundId}/{nickName}/prev", method = RequestMethod.GET)
    public RoundJson findPrevTipp(
                @PathVariable("roundId") Long roundId,
                @PathVariable("nickName") String nickName) {
        return officeTippService.findPrevTipp(roundId, nickName).orElse(null);
    }

    @RequestMapping(value = "/team/all", method = RequestMethod.GET)
    public List<TeamJson> findAllTeams() {
        return betofficeBasicJsonService.findAllTeams();
    }

    @RequestMapping(value = "/tipp/submit", method = RequestMethod.POST, headers = { "Content-type=application/json" })
    public RoundJson submitTipp(
                @RequestBody SubmitTippRoundJson tippRoundJson,
                @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
                @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        return officeTippService.submitTipp(token, tippRoundJson);
    }

    /**
     * Convert a predefined exception to an HTTP Status code
     */
    @ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Access denied")
    @ExceptionHandler(AccessDeniedException.class)
    public void forbidden() {
    }

}
