package de.betoffice.web.json;

import java.io.Serializable;
import java.util.List;

import de.betoffice.storage.group.GroupTypeDto;
import de.betoffice.storage.team.TeamDto;

public class GroupTeamJson implements Serializable {

	private static final long serialVersionUID = 1L;

	private GroupTypeDto groupType;
	private List<TeamDto> teams;

	public GroupTypeDto getGroupType() {
		return groupType;
	}

	public void setGroupType(GroupTypeDto groupType) {
		this.groupType = groupType;
	}

	public List<TeamDto> getTeams() {
		return teams;
	}

	public void setTeams(List<TeamDto> teams) {
		this.teams = teams;
	}

}
