package de.betoffice.web.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SeasonGroupTeamJson implements Serializable {

	private static final long serialVersionUID = 1L;

	private SeasonJson seasonJson;
	private List<GroupTeamJson> groupTeams = new ArrayList<GroupTeamJson>();

	public SeasonJson getSeasonJson() {
		return seasonJson;
	}

	public void setSeasonJson(SeasonJson seasonJson) {
		this.seasonJson = seasonJson;
	}

	public List<GroupTeamJson> getGroupTeams() {
		return groupTeams;
	}

	public void setGroupTeams(List<GroupTeamJson> groupTeams) {
		this.groupTeams = groupTeams;
	}

}
