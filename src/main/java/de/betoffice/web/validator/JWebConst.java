/*
 * $Id$
 * ============================================================================
 * Project betoffice-jweb
 * Copyright (c) 2000-2012 by Andre Winkler. All rights reserved.
 * ============================================================================
 *          GNU GENERAL PUBLIC LICENSE
 *  TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package de.betoffice.web.validator;

/**
 * Some constant definitions.
 * 
 * @author by Andre Winkler, $LastChangedBy$
 * @version $LastChangedRevision$ $LastChangedDate$
 */
public class JWebConst {

    /** nick name */
    public static final String F_NICKNAME = "nickname";
    
    /** password */
    public static final String F_PASSWORD = "password";

    /** round number */
    public static final String F_ROUND = "round";

    /** championship id */
    public static final String F_CHAMPIONSHIP = "championship";

    /** match count */
    public static final String F_MATCH_COUNT = "matchCount";

    /** homeGoals{0..N} */
    public static final String F_HOME_GOALS = "homeGoals";

    /** guestGoals{0..N} */
    public static final String F_GUEST_GOALS = "guestGoals";

    /* The hidden elements. */

    /** match{1..N} */
    public static final String F_MATCH = "match";

    /** matchId{0..N} */
    public static final String F_MATCH_ID = "matchId";

    /** homeTeam{0..N} */
    public static final String F_HOME_TEAM = "homeTeam";

    /** homeTeamId{0..N} */
    public static final String F_HOME_TEAM_ID = "homeTeamId";

    /** guestTeam{0..N} */
    public static final String F_GUEST_TEAM = "guestTeam";

    /** guestTeamId{0..N} */
    public static final String F_GUEST_TEAM_ID = "guestTeamId";

}
