/*
 * $Id$
 * ============================================================================
 * Project betoffice-jweb
 * Copyright (c) 2000-2013 by Andre Winkler. All rights reserved.
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

import static org.fest.assertions.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.betoffice.web.validator.EvaluateTippRequestValidator;
import de.betoffice.web.validator.JWebConst;
import de.betoffice.web.validator.Messages;
import de.betoffice.web.validator.RequestAttributes;
import de.betoffice.web.validator.RequestValidator;

/**
 * Evaluate a tipp form.
 *
 * @author by Andre Winkler, $LastChangedBy$
 * @version $LastChangedRevision$
 *          $LastChangedDate$
 */
public class RequestValidatorTest {
    
    @Test
    public void testRequestValidatorEvaluateTippRequestValidator() {
        Map<String, String[]> map = new HashMap<String, String[]>();
        map.put(JWebConst.F_NICKNAME, new String[] { "frogger" });
        map.put(JWebConst.F_PASSWORD, new String[] { "hefeweizen" });
        map.put(JWebConst.F_CHAMPIONSHIP, new String[] { "4711" });

        RequestAttributes requestAttributes = new RequestAttributes(map);
        EvaluateTippRequestValidator evaluateTippRequestValidator = new EvaluateTippRequestValidator();

        RequestValidator requestValidator = evaluateTippRequestValidator
                .createRequestValidator();
        
        Messages messages = requestValidator.validate(requestAttributes);
        assertThat(messages.size()).isEqualTo(2);
        assertThat(messages.get(0).getText()).isEqualTo("It is a mandatory field!");
        assertThat(messages.get(0).getKey()).isEqualTo("matchCount");
        assertThat(messages.get(1).getText()).isEqualTo("It is a mandatory field!");
        assertThat(messages.get(1).getKey()).isEqualTo("round");
    }

}
