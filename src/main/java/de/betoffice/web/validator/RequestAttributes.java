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

import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * Memory map for request parameters.
 * 
 * @author by Andre Winkler, $LastChangedBy$
 * @version $LastChangedRevision$ $LastChangedDate$
 */
public class RequestAttributes {

    private final Map<String, String[]> parameters;

    public RequestAttributes(final Map<String, String[]> _parameters) {
        if (_parameters == null) {
            throw new NullPointerException();
        }
        parameters = _parameters;
    }

    public String getValue(final String key) {
        String[] values = parameters.get(key);

        String result = null;
        if (values != null && values.length == 1) {
            result = values[0];
        } else if (values != null && values.length != 1) {
            result = StringUtils.join(values);
        }

        return result;
    }

    public String[] getValues(final String key) {
        return parameters.get(key);
    }

    public boolean isArray(final String key) {
        return parameters.get(key).length > 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RequestAttributes=[");
        for (String key : parameters.keySet()) {
            String[] values = parameters.get(key);
            for (String value : values) {
                sb.append("key=").append(key).append(", value=").append(value).append("; ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

}
