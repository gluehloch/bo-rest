/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2000-2021 by Andre Winkler. All
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

package de.betoffice.web;

import java.util.Optional;

import de.winkler.betoffice.storage.Community;
import de.winkler.betoffice.storage.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import de.betoffice.web.json.CommunityJson;
import de.betoffice.web.json.builder.CommunityJsonMapper;
import de.winkler.betoffice.service.CommunityService;
import de.winkler.betoffice.service.SeasonManagerService;
import de.winkler.betoffice.storage.CommunityFilter;
import de.winkler.betoffice.storage.Season;

@Component
public class DefaultBetofficeCommunityService implements BetofficeCommunityService {

	@Autowired
	private CommunityService communityService;

	@Autowired
	private SeasonManagerService seasonManagerService;
	
	@Override
	public Page<CommunityJson> findCommunities(CommunityFilter communityFilter, Pageable pageable) {
		return communityService.findCommunities(communityFilter, pageable).map(CommunityJsonMapper::map);
	}

    @Override
    public CommunityJson createCommunity(CommunityJson community) {
        Optional<User> communityManager = communityService.findUser(community.getCommunityManager().getNickname());
        Optional<Community> communityExists = communityService.find(community.getShortName());
        Optional<Season> communitySeason = seasonManagerService.findSeasonByName(community.getSeason().getName(), community.getSeason().getYear());
        
        community.getSeason();
        community.getCommunityManager();
        community.getName();
        community.getShortName();
        
        communityService.create(season, name, shortName, managerNickname);
        // TODO Auto-generated method stub
        return null;
    }

}
