/*
 * $Id: RequestValidatorBasicTest.java 3565 2013-02-19 19:53:50Z andrewinkler $
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

import org.junit.Before;
import org.junit.Test;

import de.betoffice.web.validator.Messages;
import de.betoffice.web.validator.RequestAttributes;
import de.betoffice.web.validator.RequestValidator;
import de.betoffice.web.validator.Message.Type;

/**
 * Evaluate a tipp form.
 * 
 * @author by Andre Winkler, $LastChangedBy: andrewinkler $
 * @version $LastChangedRevision: 3565 $
 *          $LastChangedDate: 2013-02-19 20:53:50 +0100 (Tue, 19 Feb 2013) $
 */
public class RequestValidatorBasicTest {

    private RequestValidator requestValidator;

    @Before
    public void setUp() {
        requestValidator = new RequestValidator();
        requestValidator.add("param", String.class, true);
    }

    @Test
    public void testRequestValidatorMissingMandatoryParameter() {
        Map<String, String[]> params = new HashMap<String, String[]>();
        RequestAttributes ra = new RequestAttributes(params);

        Messages messages = requestValidator.validate(ra);

        assertThat(!messages.isOk()).isTrue();
        assertThat(messages.get(0).getKey()).isEqualTo("param");
        assertThat(messages.get(0).getType()).isEqualTo(
                Type.MANDATORY_BUT_BLANK);
        assertThat(messages.get(0).getText()).isEqualTo(
                "It is a mandatory field!");
    }

    @Test
    public void testRequestValidatorWithJavascriptInput() {
        Map<String, String[]> params = new HashMap<String, String[]>();
        RequestAttributes ra = new RequestAttributes(params);
        params.put("param",
                new String[] { "<script>alert(\"Hallo Andre\");</script>" });

        validateErrorMessage(ra);
    }

    @Test
    public void testRequestValidatorWithIllegalCharacters() {
        RequestAttributes ra = null;

        ra = createRequestAttributes("<");
        validateErrorMessage(ra);

        ra = createRequestAttributes(">");
        validateErrorMessage(ra);
        
        ra = createRequestAttributes("\"");
        validateErrorMessage(ra);
    }

    private void validateErrorMessage(RequestAttributes ra) {
        Messages messages = requestValidator.validate(ra);
        assertThat(messages.isNotOk()).isTrue();
        assertThat(messages.get(0).getKey()).isEqualTo("param");
        assertThat(messages.get(0).getType()).isEqualTo(Type.ILLEGAL_CHARACTER);
    }

    private RequestAttributes createRequestAttributes(String character) {
        Map<String, String[]> params = new HashMap<String, String[]>();
        RequestAttributes ra = new RequestAttributes(params);
        params.put("param", new String[] { character });
        return ra;
    }

}
