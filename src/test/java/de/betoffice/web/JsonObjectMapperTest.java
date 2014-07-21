/*
 * $Id: JsonObjectMapperTest.java 3563 2013-02-19 17:39:15Z andrewinkler $
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
 *
 */
package de.betoffice.web;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.betoffice.web.validator.Message;
import de.betoffice.web.validator.Messages;
import de.betoffice.web.validator.Message.Type;

/**
 * Test of Jacksons ObjectMapper.
 *
 * @author by Andre Winkler, $LastChangedBy: andrewinkler $
 * @version $LastChangedRevision: 3563 $
 *          $LastChangedDate: 2013-02-19 18:39:15 +0100 (Tue, 19 Feb 2013) $
 */
@Ignore
public class JsonObjectMapperTest {

    @Test
    public void testJsonObjectMapper_message() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Message msg = new Message(Type.OK, "ok", "text");

        assertThat(mapper.writeValueAsString(msg)).isEqualTo(
                "{\"type\":\"OK\",\"key\":\"ok\",\"text\":\"text\"}");
    }

    @Test
    public void testJsonObjectMapper_messages() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Message msg = new Message(Type.OK, "ok", "text");
        Messages messages = new Messages(msg);

        assertThat(mapper.writeValueAsString(messages))
                .isEqualTo(
                        "{\"messages\":"
                                + "[{\"type\":\"OK\",\"key\":\"ok\",\"text\":\"text\"}],"
                                + "\"ok\":true}");
    }

    @Test
    public void testJsonObjectMapper_with_many_messages() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Messages messages = new Messages();
        messages.add(new Message(Type.ERROR, "user_name",
                "Authentication failed"));
        messages.add(new Message(Type.ERROR, "pwd", "Authentication failed"));

        assertThat(mapper.writeValueAsString(messages))
                .isEqualTo(
                        "{\"messages\":["
                                + "{\"type\":\"ERROR\",\"key\":\"user_name\",\"text\":\"Authentication failed\"},"
                                + "{\"type\":\"ERROR\",\"key\":\"pwd\",\"text\":\"Authentication failed\"}"
                                + "]," + "\"ok\":false}");
    }

}
