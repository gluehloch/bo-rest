/*
 * ============================================================================
 * Project betoffice-jweb
 * Copyright (c) 2000-2015 by Andre Winkler. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

import de.betoffice.web.validator.Message.Type;

/**
 * Collects some {@link Message}s.
 *
 * @author by Andre Winkler
 */
public class Messages {

    private List<Message> messages = new ArrayList<>();

    public Messages(final Type _type, final String _key, final String _message) {
        this(new Message(_type, _key, _message));
    }

    public Messages(final Message _msg) {
        this();
        messages.add(_msg);
    }

    public Messages() {
    }

    public void add(final Message _message) {
        messages.add(_message);
    }

    public Message get(final int _index) {
        return messages.get(_index);
    }

    public int size() {
        return messages.size();
    }

    public List<Message> getMessages() {
        return messages;
    }

    /**
     * All messages give an ok?
     *
     * @return Returns <code>true</code>, if all messages are of type {@link Type#OK}.
     */
    public boolean isOk() {
        boolean ok = true;
        for (Message msg : messages) {
            ok = Message.Type.OK.equals(msg.getType());
            if (!ok) {
                break;
            }
        }
        return ok;
    }

    /**
     * @see #isOk()
     * @return ok or not ok
     */
    public boolean isNotOk() {
        return !isOk();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Messages=[");
        for (Message message : messages) {
            sb.append(message).append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

}
