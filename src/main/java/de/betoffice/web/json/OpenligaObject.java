/*
 * ============================================================================
 * Project betoffice-jadmin Copyright (c) 2013-2015 by Andre Winkler. All rights
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
package de.betoffice.web.json;

/**
 * Describes an object from the openligadb.
 *
 * @author Andre Winkler
 */
public interface OpenligaObject {

    /**
     * An ID for the openligadb object. Could be <code>null</code>.
     * 
     * @param openligaid
     *            An openligadb object id
     */
    public void setOpenligaid(Long openligaid);

    /**
     * An ID for the openligadb object.
     * 
     * @return An openligadb object id
     */
    public Long getOpenligaid();

}
