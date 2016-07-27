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
 * Defines some common message type for the response of a (HTTP) request. Take
 * care: The name of the types is used by the Javascript frontend to identify
 * the correct message for the user.
 * 
 * @author by Andre Winkler, $LastChangedBy$
 * @version $LastChangedRevision$ $LastChangedDate$
 */
public class Message {

    public enum Type {
        /** Some ordinary types */
        OK,
        INFO,
        WARNING,
        ERROR,
        FATAL,
        
        /** Illegal request input */
        ILLEGAL_CHARACTER,
        MANDATORY_BUT_BLANK,
        
        /** Login usecase */
        LOGIN_INVALID,
        LOGGED_IN,
        NOT_LOGGED_IN,
        
        /** Tipp mailer problems */
        MAILILNG_FAILED
    }

    private final Type type;

    private final String key;

    private final String text;

    public Message(final Type _type, final String _key, final String _text) {
        type = _type;
        key = _key;
        text = _text;
    }

    public Type getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

    public static Type valueOf(String type) {
        return Type.valueOf(type);
    }

    @Override
    public String toString() {
        return "Message [type=" + type + ", key=" + key + ", text=" + text
                + "]";
    }

}
