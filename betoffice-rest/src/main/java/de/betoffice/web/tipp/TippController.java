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

package de.betoffice.web.tipp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.betoffice.web.BetofficeHttpConsts;
import de.betoffice.web.json.RoundJson;

@CrossOrigin
@RestController
@RequestMapping("/office")
public class TippController {

    private final OfficeTippService officeTippService;

    @Autowired
    public TippController(OfficeTippService officeTippService) {
        this.officeTippService = officeTippService;
    }

    /**
     * Returns the tipp of a user for a round.
     *
     * @param  roundId  Round ID
     * @param  nickName nick name
     * @return          The tipp of a user for a round
     */
    @GetMapping(value = "/tipp/{roundId}/{nickName}")
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
    @GetMapping(value = "/tipp/{seasonId}/{nickname}/current")
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
    @GetMapping(value = "/tipp/{roundId}/{nickName}/next")
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
    @GetMapping(value = "/tipp/{roundId}/{nickName}/prev")
    public RoundJson findPrevTipp(
            @PathVariable("roundId") Long roundId,
            @PathVariable("nickName") String nickName) {
        return officeTippService.findPrevTipp(roundId, nickName).orElse(null);
    }

    @RequestMapping(value = "/tipp/submit", method = RequestMethod.POST, headers = { "Content-type=application/json" })
    public RoundJson submitTipp(
            @RequestBody SubmitTippRoundJson tippRoundJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME) String nickname) {

        return officeTippService.submitTipp(token, tippRoundJson);
    }

}
