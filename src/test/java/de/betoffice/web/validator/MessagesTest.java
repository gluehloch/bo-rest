/*
 * $Id: MessagesTest.java 3565 2013-02-19 19:53:50Z andrewinkler $
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

import org.junit.Test;

import de.betoffice.web.validator.Message.Type;

/**
 * Test case for {@link Messages#isOk()}.
 *
 * @author by Andre Winkler, $LastChangedBy: andrewinkler $
 * @version $LastChangedRevision: 3565 $
 *          $LastChangedDate: 2013-02-19 20:53:50 +0100 (Tue, 19 Feb 2013) $
 */
public class MessagesTest {

    @Test
    public void testMessagesIsOk_withNoMessages() {
        Messages messages = new Messages();
        assertThat(messages.isOk()).isTrue();
    }

    @Test
    public void testMessagesIsOk_withOneMessage() {
        Messages messages = new Messages();
        messages.add(new Message(Type.OK, "key", "Everything is fine."));
        assertThat(messages.isOk()).isTrue();
        
        messages = new Messages();
        messages.add(new Message(Type.ERROR, "key", "Error!"));
        assertThat(messages.isOk()).isFalse();

        messages = new Messages();
        messages.add(new Message(Type.WARNING, "key", "Error!"));
        assertThat(messages.isOk()).isFalse();

        messages = new Messages();
        messages.add(new Message(Type.INFO, "key", "Error!"));
        assertThat(messages.isOk()).isFalse();
    }

    @Test
    public void testMessagesIsOk_WithSomeMessages() {
        Messages messages = new Messages();
        messages.add(new Message(Type.OK, "key", "Everything is fine."));
        messages.add(new Message(Type.OK, "key", "Everything is fine."));
        assertThat(messages.isOk()).isTrue();

        messages = new Messages();
        messages.add(new Message(Type.OK, "key", "Everything is fine."));
        messages.add(new Message(Type.ERROR, "key", "Error!"));
        assertThat(messages.isOk()).isFalse();
        
        messages = new Messages();
        messages.add(new Message(Type.WARNING, "key", "Warning!"));
        messages.add(new Message(Type.ERROR, "key", "Error!"));
        assertThat(messages.isOk()).isFalse();
    }

    
}
