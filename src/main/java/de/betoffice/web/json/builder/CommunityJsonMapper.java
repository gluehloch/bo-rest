/*
 * ============================================================================
 * Project betoffice-jweb Copyright (c) 2013-2023 by Andre Winkler. All rights
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

import de.betoffice.web.json.CommunityJson;
import de.betoffice.web.json.PartyJson;
import de.betoffice.web.json.SeasonJson;
import de.winkler.betoffice.storage.Community;

public class CommunityJsonMapper {

    public static CommunityJson map(Community community) {
        return map(community, new CommunityJson());
    }

    public static CommunityJson map(Community community, CommunityJson json) {
        json.setId(community.getId());
        json.setName(community.getName());
        json.setYear(community.getYear());
        json.setShortName(community.getReference().getShortName());
        json.setCommunityManager(PartyJsonMapper.map(community.getCommunityManager(), new PartyJson()));
        json.setSeason(SeasonJsonMapper.map(community.getSeason(), new SeasonJson()));
        return json;
    }

}
