/*
 * $Id$
 * ============================================================================
 * Project betoffice-jweb
 * Copyright (c) 2000-2012 by Andre Winkler. All rights reserved.
 * ============================================================================
 *          GNU GENERAL PUBLIC LICENSE
 *  TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package de.betoffice.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.betoffice.web.TippFormData.GameFormData;
import de.betoffice.web.validator.JWebConst;
import de.betoffice.web.validator.RequestAttributes;

/**
 * Maps parameters parameters to object {@link TippFormData}.
 * 
 * @author by Andre Winkler, $LastChangedBy$
 * @version $LastChangedRevision$ $LastChangedDate$
 */
public class TippFormDataConverter {

    private static final Logger log = LoggerFactory
            .getLogger(TippFormDataConverter.class);

    /** Maximum number of matches per tipp form. */
    private static final int MAX_MATCH_COUNT = 20;

    /**
     * Convert request parameters to object TippFormData.
     * 
     * @param parameters
     *            A parameter map
     * @return the pumped object
     * @throws TippRequestInvalidException
     *             There is something wrong with the tipp form.
     */
    public TippFormData convert(final RequestAttributes parameters)
            throws TippRequestInvalidException {

        String nickNameParam = parameters.getValue(JWebConst.F_NICKNAME);
        String passwordParam = parameters.getValue(JWebConst.F_PASSWORD);
        String championshipIdParam = parameters
                .getValue(JWebConst.F_CHAMPIONSHIP);
        String matchCountParam = parameters.getValue(JWebConst.F_MATCH_COUNT);
        String roundNrParam = parameters.getValue(JWebConst.F_ROUND);

        TippFormData tippFormData = null;
        try {
            long championshipId = Long.parseLong(championshipIdParam);
            int matchCount = Integer.parseInt(matchCountParam);
            if (matchCount > MAX_MATCH_COUNT) {
                throw new IllegalArgumentException(
                        "Parameter maxCount is greater than MAX_MATCH_COUNT.");
            }

            int roundNr = Integer.parseInt(roundNrParam);

            tippFormData = new TippFormData();
            tippFormData.setChampionship(championshipId);
            tippFormData.setMatches(matchCount);
            tippFormData.setRoundNr(roundNr);
            tippFormData.setNickname(nickNameParam);
            tippFormData.setPassword(passwordParam);

            for (int i = 0; i < matchCount; i++) {
                int index = i + 1;
                String matchNrParam = parameters.getValue(JWebConst.F_MATCH
                        + index);
                String matchIdParam = parameters.getValue(JWebConst.F_MATCH_ID
                        + index);
                String homeTeamIdParam = parameters
                        .getValue(JWebConst.F_HOME_TEAM_ID + index);
                String guestTeamIdParam = parameters
                        .getValue(JWebConst.F_GUEST_TEAM_ID + index);
                String homeTeamNameParam = parameters
                        .getValue(JWebConst.F_HOME_TEAM + index);
                String guestTeamNameParam = parameters
                        .getValue(JWebConst.F_GUEST_TEAM + index);
                String homeTeamGoalsParam = parameters
                        .getValue(JWebConst.F_HOME_GOALS + index);
                String guestTeamGoalsParam = parameters
                        .getValue(JWebConst.F_GUEST_GOALS + index);

                GameFormData gameFormData = new GameFormData();
                gameFormData.setMatchNr(Integer.parseInt(matchNrParam));
                gameFormData.setMatchId(Long.parseLong(matchIdParam));
                gameFormData.setHomeTeamId(Long.parseLong(homeTeamIdParam));
                gameFormData.setGuestTeamId(Long.parseLong(guestTeamIdParam));
                gameFormData.setHomeTeam(homeTeamNameParam);
                gameFormData.setGuestTeam(guestTeamNameParam);
                gameFormData.setHomeGoals(Integer.parseInt(homeTeamGoalsParam));
                gameFormData.setGuestGoals(Integer
                        .parseInt(guestTeamGoalsParam));

                tippFormData.add(gameFormData);
            }
        } catch (NumberFormatException ex) {
            log.error("Catched an exception", ex);
            throw new TippRequestInvalidException();
        }

        return tippFormData;
    }

}
