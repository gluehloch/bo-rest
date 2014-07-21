/*
 * $Id: EvaluateTippAuthRequestValidator.java 3800 2013-08-20 05:21:46Z andrewinkler $
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
 * Checks the user authentication parameters.
 *
 * @author by Andre Winkler, $LastChangedBy: andrewinkler $
 * @version $LastChangedRevision: 3800 $ $LastChangedDate: 2013-08-20 07:21:46 +0200 (Tue, 20 Aug 2013) $
 */
public class EvaluateTippAuthRequestValidator {

    /**
     * Creates a RequestValidator to validate a tipp form.
     *
     * @return a request validator
     */
    public RequestValidator createRequestValidator() {
        RequestValidator requestValidator = new RequestValidator();

        requestValidator.addStringParameter(JWebConst.F_NICKNAME, true);
        requestValidator.addStringParameter(JWebConst.F_PASSWORD, true);
        return requestValidator;
    }

}
