/*
 * $Id: EvaluateTippServletJsonTest.java 3703 2013-04-03 04:09:50Z andrewinkler $
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

package de.betoffice.web;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;

import de.betoffice.web.validator.Message;

/**
 * Evaluate a tipp form.
 *
 * @author by Andre Winkler, $LastChangedBy: andrewinkler $
 * @version $LastChangedRevision: 3703 $ $LastChangedDate: 2013-04-03 06:09:50 +0200 (Wed, 03 Apr 2013) $
 */
public class EvaluateTippServletJsonTest {

    private static final String BASE_URL = "http://localhost:8080/betoffice-jweb";

    private WebClient webClient;

    @Before
    public void setUp() {
        webClient = new WebClient();
    }

    @After
    public void tearDown() {
        webClient.closeAllWindows();
    }

    @Test @Ignore
    public void testEvaluateTippServletPostNoDataAndGetAnErrorMessage()
            throws Exception {

        WebRequest webRequest = new WebRequest(new URL(BASE_URL
                + "/tipp/submit"), HttpMethod.POST);

        Map<String, Object> result = postRequest(webRequest);
        JsonMessageWrapper messageWrapper = new JsonMessageWrapper(result);

        assertThat(result.get("ok")).isInstanceOf(Boolean.class);
        assertThat(messageWrapper.isOk()).isEqualTo(false);
        assertThat(result.get("notOk")).isInstanceOf(Boolean.class);
        assertThat(messageWrapper.isNotOk()).isEqualTo(true);

        Object messages = result.get("messages");
        assertThat(messages).isInstanceOf(ArrayList.class);

        // {type=MANDATORY_BUT_BLANK, key=user_name, text=It is a mandatory field!}        
        Message messageAtIndex = messageWrapper.getMessageAtIndex(0);
        assertThat(messageAtIndex.getType()).isEqualTo(Message.Type.MANDATORY_BUT_BLANK);
        assertThat(messageAtIndex.getKey()).isEqualTo("user_name");
        assertThat(messageAtIndex.getText()).isEqualTo("It is a mandatory field!");

        // {"type":"MANDATORY_BUT_BLANK","key":"pwd","text":"It is a mandatory field!"}
        Message messageAtIndex2 = messageWrapper.getMessageAtIndex(1);
        assertThat(messageAtIndex2.getType()).isEqualTo(Message.Type.MANDATORY_BUT_BLANK);
        assertThat(messageAtIndex2.getKey()).isEqualTo("pwd");
        assertThat(messageAtIndex2.getText()).isEqualTo("It is a mandatory field!");

        // {"type":"MANDATORY_BUT_BLANK","key":"matchCount","text":"It is a mandatory field!"}
        Message messageAtIndex3 = messageWrapper.getMessageAtIndex(2);
        assertThat(messageAtIndex3.getType()).isEqualTo(Message.Type.MANDATORY_BUT_BLANK);
        assertThat(messageAtIndex3.getKey()).isEqualTo("matchCount");
        assertThat(messageAtIndex3.getText()).isEqualTo("It is a mandatory field!");

        // {"type":"MANDATORY_BUT_BLANK","key":"round","text":"It is a mandatory field!"}
        Message messageAtIndex4 = messageWrapper.getMessageAtIndex(3);
        assertThat(messageAtIndex4.getType()).isEqualTo(Message.Type.MANDATORY_BUT_BLANK);
        assertThat(messageAtIndex4.getKey()).isEqualTo("round");
        assertThat(messageAtIndex4.getText()).isEqualTo("It is a mandatory field!");

        // {"type":"MANDATORY_BUT_BLANK","key":"championship","text":"It is a mandatory field!"}
        Message messageAtIndex5 = messageWrapper.getMessageAtIndex(4);
        assertThat(messageAtIndex5.getType()).isEqualTo(Message.Type.MANDATORY_BUT_BLANK);
        assertThat(messageAtIndex5.getKey()).isEqualTo("championship");
        assertThat(messageAtIndex5.getText()).isEqualTo("It is a mandatory field!");

        // {"type":"ERROR","key":"tipp","text":"Die Benutzereingaben konnten nicht verarbeitet werden!"}
        Message messageAtIndex6 = messageWrapper.getMessageAtIndex(5);
        assertThat(messageAtIndex6.getType()).isEqualTo(Message.Type.ERROR);
        assertThat(messageAtIndex6.getKey()).isEqualTo("tipp");
        assertThat(messageAtIndex6.getText()).isEqualTo("Die Benutzereingaben konnten nicht verarbeitet werden!");

        /*
         * + "?" + JWebConst.F_CHAMPIONSHIP + "=55&" + JWebConst.F_MATCH_COUNT +
         * "=1&" + JWebConst.F_ROUND + "=1&" + JWebConst.F_MATCH + "1=1");
         */
    }

    private Map<String, Object> postRequest(WebRequest webRequest)
            throws IOException, JsonParseException, JsonMappingException {

        WebResponse webResponse = webClient.loadWebResponse(webRequest);
        assertThat(accessControlAllowOrigin(webResponse)).isEqualTo("*");
        assertThat(contentType(webResponse)).isEqualTo(
                "application/json;charset=UTF-8");

        // Expecting a HTTP OK
        int statusCode = webResponse.getStatusCode();
        assertThat(statusCode).isEqualTo(200);
        String jsonResponse = webResponse.getContentAsString();

        @SuppressWarnings("unchecked")
        Map<String, Object> result = new ObjectMapper().readValue(jsonResponse,
                HashMap.class);

        return result;
    }

    private String contentType(WebResponse webResponse) {
        return webResponse.getResponseHeaderValue("Content-Type");
    }

    private String accessControlAllowOrigin(WebResponse webResponse) {
        return webResponse
                .getResponseHeaderValue("Access-Control-Allow-Origin");
    }

}
