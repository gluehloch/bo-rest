/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2013-2023 by Andre Winkler. All rights
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

package de.betoffice.web.json.builder;

import java.util.List;

import de.betoffice.web.json.GroupTypeJson;
import de.winkler.betoffice.storage.GroupType;

/**
 * Map a {@link GroupType} to {@link GroupTypeJson}.
 * 
 * @author Andre Winkler
 */
public class GroupTypeJsonMapper {

    public static GroupTypeJson map(GroupType groupType, GroupTypeJson groupTypeJson) {
        groupTypeJson.setId(groupType.getId());
        groupTypeJson.setName(groupType.getName());
        groupTypeJson.setType(groupType.getType().name());
        return groupTypeJson;
    }

    public static List<GroupTypeJson> map(List<GroupType> groupTypes) {
        return groupTypes.stream().map(GroupTypeJsonMapper::map).toList();
    }
    
    private static GroupTypeJson map(GroupType groupType) {
    	return map(groupType, new GroupTypeJson());
    }

}
