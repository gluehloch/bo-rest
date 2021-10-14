/*
 * ============================================================================
 * Project betoffice-jweb Copyright (c) 2015-2021 by Andre Winkler. All rights
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

import de.betoffice.web.json.CommunityJson;
import de.betoffice.web.json.builder.CommunityJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.winkler.betoffice.storage.Community;

/**
 * Community management.
 * 
 * @author winkler
 */
@CrossOrigin
@RestController
@RequestMapping("/community-admin")
public class CommunityController {

    @Autowired
    private BetofficeCommunityService communityService;

    @GetMapping(value = "/communities", headers = { "Content-type=application/json" })
    public ResponseEntity<Page<CommunityJson>> findCommunities(
            Pageable pageable,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        Page<CommunityJson> communities = communityService.findCommunities("", pageable).map(CommunityJsonMapper::map);
        return ResponseEntity.ok(communities);
    }

}
