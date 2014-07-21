/*
 * $Id: TippFormDataConverterTest.java 3805 2013-08-24 06:45:57Z andrewinkler $
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
package de.betoffice.web;

import static org.fest.assertions.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.betoffice.web.validator.JWebConst;
import de.betoffice.web.validator.RequestAttributes;

/**
 * Test for class {@link TippFormDataConverter}.
 * 
 * @author by Andre Winkler, $LastChangedBy: andrewinkler $
 * @version $LastChangedRevision: 3805 $
 *          $LastChangedDate: 2013-08-24 08:45:57 +0200 (Sat, 24 Aug 2013) $
 */
@Ignore
public class TippFormDataConverterTest {

    private RequestAttributes requestAttributes;

    @Before
    public void before() {
        Map<String, String[]> params = new HashMap<String, String[]>();
        requestAttributes = new RequestAttributes(params);

        params.put(JWebConst.F_NICKNAME, new String[] { "username" });
        params.put(JWebConst.F_PASSWORD, new String[] { "password" });
        params.put(JWebConst.F_CHAMPIONSHIP, new String[] { "4711" });
        params.put(JWebConst.F_MATCH_COUNT, new String[] { "2" });
        params.put(JWebConst.F_ROUND, new String[] { "1" });

        params.put(JWebConst.F_MATCH + 1, new String[] { "1" });
        params.put(JWebConst.F_MATCH_ID + 1, new String[] { "100" });
        params.put(JWebConst.F_HOME_TEAM_ID + 1, new String[] { "1001" });
        params.put(JWebConst.F_HOME_TEAM + 1, new String[] { "RWE" });
        params.put(JWebConst.F_GUEST_TEAM_ID + 1, new String[] { "1002" });
        params.put(JWebConst.F_GUEST_TEAM + 1, new String[] { "Schalke 04" });
        params.put(JWebConst.F_HOME_GOALS + 1, new String[] { "2" });
        params.put(JWebConst.F_GUEST_GOALS + 1, new String[] { "1" });
        
        params.put(JWebConst.F_MATCH + 2, new String[] { "2" });
        params.put(JWebConst.F_MATCH_ID + 2, new String[] { "200" });
        params.put(JWebConst.F_HOME_TEAM_ID + 2, new String[] { "1003" });
        params.put(JWebConst.F_HOME_TEAM + 2, new String[] { "Dortmund" });
        params.put(JWebConst.F_GUEST_TEAM_ID + 2, new String[] { "1004" });
        params.put(JWebConst.F_GUEST_TEAM + 2, new String[] { "Bochum" });
        params.put(JWebConst.F_HOME_GOALS + 2, new String[] { "1" });
        params.put(JWebConst.F_GUEST_GOALS + 2, new String[] { "1" });
    }

    @Test
    public void testTippFormDataConverter() throws Exception {
        TippFormDataConverter converter = new TippFormDataConverter();
        TippFormData tippFormData = converter.convert(requestAttributes);

        assertThat(tippFormData.getNickname()).isEqualTo("username");
        assertThat(tippFormData.getChampionship()).isEqualTo(4711);
        assertThat(tippFormData.getMatches()).isEqualTo(2);
        assertThat(tippFormData.getRoundNr()).isEqualTo(1);
    }

}
