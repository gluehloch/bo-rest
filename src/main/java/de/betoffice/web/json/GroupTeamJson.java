package de.betoffice.web.json;

import java.io.Serializable;
import java.util.List;

public class GroupTeamJson implements Serializable {

	private static final long serialVersionUID = 1L;

	private GroupTypeJson groupType;
	private List<TeamJson> teams;

	public GroupTypeJson getGroupType() {
		return groupType;
	}

	public void setGroupType(GroupTypeJson groupType) {
		this.groupType = groupType;
	}

	public List<TeamJson> getTeams() {
		return teams;
	}

	public void setTeams(List<TeamJson> teams) {
		this.teams = teams;
	}

}
