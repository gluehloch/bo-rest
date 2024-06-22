/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2013-2022 by Andre Winkler. All
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

package de.betoffice.web.json.builder;

import de.betoffice.web.json.SecurityTokenJson;
import de.winkler.betoffice.service.SecurityToken;
import de.winkler.betoffice.storage.enums.RoleType;

/**
 * Map a {@link SecurityToken} to {@link SecurityTokenJson}.
 *
 * @author Andre Winkler
 */
public class SecurityTokenJsonMapper {

    public static SecurityTokenJson map(SecurityToken securityToken, SecurityTokenJson securityTokenJson) {
        securityTokenJson.setLoginTime(securityToken.getLoginTime());
        securityTokenJson.setNickname(securityToken.getUser().getNickname().value());

        if (securityToken.getRoleTypes().contains(RoleType.ADMIN)) {
            securityTokenJson.setRole(RoleType.ADMIN.name());
        } else if (securityToken.getRoleTypes().contains(RoleType.TIPPER)) {
            securityTokenJson.setRole(RoleType.TIPPER.name());
        } else {
            securityTokenJson.setRole(null);
        }

        securityTokenJson.setToken(securityToken.getToken());
        return securityTokenJson;
    }

}
