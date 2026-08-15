package de.betoffice.web.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.betoffice.storage.season.SeasonDto;

public class SeasonGroupTeamJson implements Serializable {

	private static final long serialVersionUID = 1L;

	private SeasonDto seasonJson;
	private List<GroupTeamJson> groupTeams = new ArrayList<GroupTeamJson>();

	public SeasonDto getSeasonJson() {
		return seasonJson;
	}

	public void setSeasonJson(SeasonDto seasonJson) {
		this.seasonJson = seasonJson;
	}

	public List<GroupTeamJson> getGroupTeams() {
		return groupTeams;
	}

	public void setGroupTeams(List<GroupTeamJson> groupTeams) {
		this.groupTeams = groupTeams;
	}

}
