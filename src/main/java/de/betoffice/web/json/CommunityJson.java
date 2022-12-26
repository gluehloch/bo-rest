package de.betoffice.web.json;

import java.io.Serializable;

public class CommunityJson extends AbstractIdentifier implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String shortName;
    private PartyJson communityManager;
    private SeasonJson season;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public PartyJson getCommunityManager() {
        return communityManager;
    }

    public void setCommunityManager(PartyJson communityManager) {
        this.communityManager = communityManager;
    }

    public SeasonJson getSeason() {
        return season;
    }

    public void setSeason(SeasonJson season) {
        this.season = season;
    }

    @Override
    public String toString() {
        return "CommunityJson{" +
                "name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", communityManager=" + communityManager +
                ", season=" + season +
                '}';
    }

}
