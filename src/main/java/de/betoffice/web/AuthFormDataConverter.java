/*
 * $Id: AuthFormDataConverter.java 3801 2013-08-20 19:03:22Z andrewinkler $
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

package de.betoffice.web;

import de.betoffice.web.validator.JWebConst;
import de.betoffice.web.validator.RequestAttributes;

/**
 * Evaluate an authentication form.
 *
 * @author by Andre Winkler, $LastChangedBy: andrewinkler $
 * @version $LastChangedRevision: 3801 $ $LastChangedDate: 2013-08-20 21:03:22 +0200 (Tue, 20 Aug 2013) $
 */
public class AuthFormDataConverter {

    /**
     * Convert request parameters to object TippFormData.
     * 
     * @param parameters
     *            A parameter map
     * @return the pumped object
     * @throws TippRequestInvalidException
     *             There is something wrong with the tipp form.
     */
    public AuthFormData convert(final RequestAttributes parameters)
            throws TippRequestInvalidException {

        String nickname = parameters.getValue(JWebConst.F_NICKNAME);
        String password = parameters.getValue(JWebConst.F_PASSWORD);

        AuthFormData authFormData = new AuthFormData();
        authFormData.setNickname(nickname);
        authFormData.setPassword(password);
        return authFormData;
    }

}
