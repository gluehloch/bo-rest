/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2000-2024 by Andre Winkler. All
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

package de.betoffice.web.tipp;

import java.util.Optional;

import de.betoffice.web.json.RoundJson;

public interface OfficeTippService {

    /**
     * Find the next round to tipp
     *
     * @param  seasonId the season id
     * @return          the round
     */
    Optional<RoundJson> findTippRound(Long seasonId);

    /**
     * Find round and tipp of a user
     *
     * @param  roundId  the round id
     * @param  nickName the nickname of the user
     * @return          the round
     */
    RoundJson findTipp(Long roundId, String nickName);

    /**
     * Find current tipp round for an user.
     *
     * @param  seasonId the season id
     * @param  nickName the nickname of the user
     * @return          the round
     */
    Optional<RoundJson> findCurrentTipp(Long seasonId, String nickName);

    /**
     * Find the next tipp round
     *
     * @param  roundId  the round id
     * @param  nickName the nickname of the user
     * @return          a round
     */
    Optional<RoundJson> findNextTipp(Long roundId, String nickName);

    /**
     * Find the prev tipp round
     *
     * @param  roundId  the round id
     * @param  nickName the nickname of the user
     * @return          a round
     */
    Optional<RoundJson> findPrevTipp(Long roundId, String nickName);

    /**
     * Submit a tipp
     *
     * @param  token         token
     * @param  tippRoundJson tipp data
     * @return               a round
     */
    RoundJson submitTipp(String token, SubmitTippRoundJson tippRoundJson);

}
