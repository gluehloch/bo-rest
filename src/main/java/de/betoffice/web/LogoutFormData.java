/*
 * $Id$
 * ============================================================================
 * Project betoffice-jweb Copyright (c) 2000-2014 by Andre Winkler. All rights
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

/**
 * Evaluate a tipp form.
 *
 * @author by Andre Winkler
 */
public class LogoutFormData {

    /** Users nick name. */
    private String nickname;

    /** The token to identify the user session. */
    private String securityToken;

    /**
     * @return the nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname
     *            the nickname to set
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return the securityToken
     */
    public String getSecurityToken() {
        return securityToken;
    }

    /**
     * @param securityToken
     *            the securityToken to set
     */
    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LogoutFormData [nickname=" + nickname + ", securityToken="
                + securityToken + "]";
    }

}
