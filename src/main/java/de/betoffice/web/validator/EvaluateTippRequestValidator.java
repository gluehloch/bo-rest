/*
 * $Id$
 * ============================================================================
 * Project betoffice-jweb
 * Copyright (c) 2000-2013 by Andre Winkler. All rights reserved.
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
package de.betoffice.web.validator;

/**
 * Creates the {@link RequestValidator} to validate a tipp form.
 *
 * @author by Andre Winkler, $LastChangedBy$
 * @version $LastChangedRevision$ $LastChangedDate$ */
public class EvaluateTippRequestValidator {

    /**
     * Creates a RequestValidator to validate a tipp form.
     *
     * @return a request validator
     */
    public RequestValidator createRequestValidator() {
        RequestValidator requestValidator = new RequestValidator();

        requestValidator.addStringParameter(JWebConst.F_NICKNAME, true);
        requestValidator.addStringParameter(JWebConst.F_PASSWORD, true);

        requestValidator.add(JWebConst.F_CHAMPIONSHIP, Long.class, true);
        requestValidator.add(JWebConst.F_ROUND, Integer.class, true);
        requestValidator.add(JWebConst.F_MATCH_COUNT, Integer.class, true);

        requestValidator.addStringParameter(JWebConst.F_HOME_TEAM, true, true);
        requestValidator.add(JWebConst.F_HOME_TEAM_ID, Long.class, true, true);
        requestValidator.add(JWebConst.F_HOME_GOALS, Integer.class, true, true);
        requestValidator.addStringParameter(JWebConst.F_GUEST_TEAM, true, true);
        requestValidator.add(JWebConst.F_GUEST_TEAM_ID, Long.class, true, true);
        requestValidator
                .add(JWebConst.F_GUEST_GOALS, Integer.class, true, true);
        return requestValidator;
    }

}
