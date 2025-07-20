/*
 * ============================================================================
 * Project betoffice-jweb Copyright (c) 2015-2024 by Andre Winkler. All rights
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

package de.betoffice.web.community;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.betoffice.web.BetofficeHttpConsts;
import de.betoffice.web.PageParam;
import de.betoffice.web.PageParamObjectMapper;
import de.betoffice.web.SortParam;
import de.betoffice.web.json.CommunityJson;
import de.betoffice.web.json.PartyJson;
import de.betoffice.web.json.SeasonJson;
import de.betoffice.web.json.builder.CommunityJsonMapper;
import de.winkler.betoffice.service.CommunityService;
import de.winkler.betoffice.storage.Community;
import de.winkler.betoffice.storage.CommunityFilter;
import de.winkler.betoffice.storage.CommunityReference;
import de.winkler.betoffice.storage.Nickname;
import de.winkler.betoffice.storage.SeasonReference;
import de.winkler.betoffice.validation.ServiceResult;

/**
 * Community management.
 * 
 * @author Andre Winkler
 */
@CrossOrigin
@RestController
@RequestMapping("/community-admin")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        ObjectMapper objectMapper = new ObjectMapper();
        binder.registerCustomEditor(PageParam.class, new PageParamObjectMapper(objectMapper));
    }

    @GetMapping(value = "/communities", headers = { "Content-type=application/json" })
    public ResponseEntity<Page<CommunityJson>> findCommunities(
            @RequestParam(required = true, name = "pageParam") PageParam pageParam,
            @RequestParam(required = false, name = "sortParam") SortParam sortParam,
            // @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        Sort sort = Sort.by(Sort.Order.asc("name"), Sort.Order.desc("shortName"));
        CommunityFilter communityFilter = new CommunityFilter();
        PageRequest pageRequest = pageParam.toPageRequest(sort);

        Page<CommunityJson> communities = communityService.findCommunities(communityFilter, pageRequest)
                .map(CommunityJsonMapper::map);
        return ResponseEntity.ok(communities);
    }

    @GetMapping(value = "/community/{communityId}", headers = { "Content-type=application/json" })
    public ResponseEntity<CommunityJson> findCommunity(
            @PathVariable("communityId") Long communityId,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        return ResponseEntity.ok(CommunityJsonMapper.map(communityService.find(communityId)));
        
    }

    @PostMapping(value = "/community", headers = { "Content-type=application/json" })
    public ResponseEntity<CommunityJson> createCommunity(
            @RequestBody CommunityJson communityJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        PartyJson communityManager = communityJson.getCommunityManager();
        SeasonJson season = communityJson.getSeason();
        String name = communityJson.getName();
        String shortName = communityJson.getShortName();
        String year = communityJson.getYear();

        CommunityReference communityReference = CommunityReference.of(shortName);
        SeasonReference seasonReference = SeasonReference.of(season.getYear(), season.getName());
        Nickname nickname = Nickname.of(communityManager.getNickname());

        ServiceResult<Community> betofficeServiceResult = communityService.create(
                communityReference,
                seasonReference,
                name,
                year,
                nickname);

        Optional<CommunityJson> community = betofficeServiceResult.result().map(CommunityJsonMapper::map);

        return community.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping(value = "/community", headers = { "Content-type=application/json" })
    public ResponseEntity<CommunityJson> updateCommunity(
            @RequestBody CommunityJson communityJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        return null;
    }

    @DeleteMapping(value = "/community", headers = { "Content-type=application/json" })
    public ResponseEntity<CommunityJson> deleteCommunity(
            @RequestBody CommunityJson communityJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        return null;
    }
}
