/*
 * $Id: JsonMessageWrapper.java 3612 2013-03-13 17:53:19Z andrewinkler $
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

import java.util.List;
import java.util.Map;

import de.betoffice.web.validator.Message;

/**
 * Evaluate a tipp form.
 *
 * @author by Andre Winkler, $LastChangedBy: andrewinkler $
 * @version $LastChangedRevision: 3612 $ $LastChangedDate: 2013-03-13 18:53:19 +0100 (Wed, 13 Mar 2013) $
 */
public class JsonMessageWrapper {

    private final Map<String, Object> jsonMessages;
    private final List<?> messagesAsList;

    public JsonMessageWrapper(Map<String, Object> _result) {
        jsonMessages = _result;
        messagesAsList = (List<?>) jsonMessages.get("messages");
    }

    public boolean isOk() {
        return ((Boolean) jsonMessages.get("ok")).booleanValue();
    }

    public boolean isNotOk() {
        return ((Boolean) jsonMessages.get("notOk")).booleanValue();
    }

    @SuppressWarnings("unchecked")
    public Message getMessageAtIndex(int index) {
        Map<String, Object> map = (Map<String, Object>) messagesAsList
                .get(index);
        Message message = new Message(Message.Type.valueOf(map.get("type")
                .toString()), map.get("key").toString(), map.get("text")
                .toString());
        return message;
    }

}