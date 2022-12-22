package de.betoffice.web.json.builder;

import de.betoffice.web.json.CommunityJson;
import de.betoffice.web.json.PartyJson;
import de.betoffice.web.json.SeasonJson;
import de.winkler.betoffice.storage.Community;

public class CommunityJsonMapper {

    public static CommunityJson map(Community community) {
        return new CommunityJsonMapper().map(community, new CommunityJson());
    }

    public CommunityJson map(Community community, CommunityJson json) {
        json.setId(community.getId());
        json.setName(community.getName());
        json.setShortName(community.getReference().getShortName());

        PartyJsonMapper partyJsonMapper = new PartyJsonMapper();
        json.setCommunityManager(partyJsonMapper.map(community.getCommunityManager(), new PartyJson()));

        SeasonJsonMapper seasonJsonMapper = new SeasonJsonMapper();
        json.setSeason(seasonJsonMapper.map(community.getSeason(), new SeasonJson()));

        return json;
    }

}
