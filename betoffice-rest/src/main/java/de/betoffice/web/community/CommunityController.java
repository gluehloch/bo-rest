/*
 * ============================================================================
 * Project betoffice-web Copyright (c) 2015-2026 by Andre Winkler. All rights
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

import de.betoffice.service.CommunityService;
import de.betoffice.service.request.CommunityCreateCommand;
import de.betoffice.storage.community.CommunityDto;
import de.betoffice.storage.community.CommunityFilter;
import de.betoffice.storage.community.entity.CommunityReference;
import de.betoffice.storage.season.entity.SeasonReference;
import de.betoffice.storage.user.entity.Nickname;
import de.betoffice.validation.ServiceResult;
import de.betoffice.web.BetofficeHttpConsts;
import de.betoffice.web.PageParam;
import de.betoffice.web.PageParamObjectMapper;
import de.betoffice.web.SortParam;
import tools.jackson.databind.ObjectMapper;

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
    public ResponseEntity<Page<CommunityDto>> findCommunities(
            @RequestParam(required = true, name = "pageParam") PageParam pageParam,
            @RequestParam(required = false, name = "sortParam") SortParam sortParam,
            // @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        final Sort sort = Sort.by(Sort.Order.asc("name"), Sort.Order.desc("shortName"));
        final CommunityFilter communityFilter = new CommunityFilter();
        final PageRequest pageRequest = pageParam.toPageRequest(sort);
        return ResponseEntity.ok(communityService.findCommunities(communityFilter, pageRequest));
    }

    @GetMapping(value = "/community/{communityId}", headers = { "Content-type=application/json" })
    public ResponseEntity<CommunityDto> findCommunity(
            @PathVariable("communityId") Long communityId,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        return ResponseEntity.ok(communityService.find(communityId));
    }

    @PostMapping(value = "/community", headers = { "Content-type=application/json" })
    public ResponseEntity<CommunityDto> createCommunity(
            @RequestBody CreateCommunityRequest createCommunityRequest,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        final CommunityReference communityReference = CommunityReference
                .of(createCommunityRequest.communityShortName());
        final SeasonReference seasonReference = SeasonReference
                .of(createCommunityRequest.seasonReferenceYear(), createCommunityRequest.seasonReferenceName());
        final Nickname nickname = Nickname
                .of(createCommunityRequest.communityManagerNickname());
        final CommunityCreateCommand createCommand = new CommunityCreateCommand(
                communityReference,
                seasonReference,
                createCommunityRequest.communityName(),
                createCommunityRequest.communityYear(),
                nickname);

        final ServiceResult<CommunityDto> serviceResult = communityService.create(createCommand);
        return serviceResult.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping(value = "/community", headers = { "Content-type=application/json" })
    public ResponseEntity<CommunityDto> updateCommunity(
            @RequestBody CommunityDto communityJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        return null;
    }

    @DeleteMapping(value = "/community", headers = { "Content-type=application/json" })
    public ResponseEntity<CommunityDto> deleteCommunity(
            @RequestBody CommunityDto communityJson,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN) String token,
            @RequestHeader(BetofficeHttpConsts.HTTP_HEADER_USER_AGENT) String userAgent) {

        return null;
    }
}
