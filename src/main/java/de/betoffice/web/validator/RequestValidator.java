/*
 * $Id: RequestValidator.java 3563 2013-02-19 17:39:15Z andrewinkler $
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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import de.betoffice.web.validator.Message.Type;

/**
 * Checks all mandatory and optional tipp form request parameters.
 * 
 * @author by Andre Winkler, $LastChangedBy: andrewinkler $
 * @version $LastChangedRevision: 3563 $
 *          $LastChangedDate: 2013-02-19 18:39:15 +0100 (Tue, 19 Feb 2013) $
 */
public class RequestValidator {

    private Map<String, ParameterDescription> parameters = new HashMap<String, ParameterDescription>();

    /**
     * Add a new HTTP parameter request type.
     *
     * @param _name Parameter name
     * @param _mandatory mandatory or not?
     */
    public void addStringParameter(final String _name, final boolean _mandatory) {

        ParameterDescription parameter = new ParameterDescription(_name,
                String.class, _mandatory, false);
        parameters.put(_name, parameter);
    }

    /**
     * Add a new HTTP parameter request type.
     *
     * @param _name Parameter name
     * @param _mandatory mandatory or not?
     * @param _array array type?
     */
    public void addStringParameter(final String _name, final boolean _mandatory,
            final boolean _array) {

        ParameterDescription parameter = new ParameterDescription(_name,
                String.class, _mandatory, _array);
        parameters.put(_name, parameter);
    }

    /**
     * Add a new HTTP parameter request type.
     *
     * @param _name Parameter name
     * @param _clazz Type
     * @param _mandatory mandatory or not?
     */
    public void add(final String _name, final Class<?> _clazz,
            final boolean _mandatory) {

        ParameterDescription parameter = new ParameterDescription(_name,
                _clazz, _mandatory, false);
        parameters.put(_name, parameter);
    }

    /**
     * Add a new HTTP parameter request type.
     *
     * @param _name Parameter name
     * @param _clazz Type
     * @param _mandatory mandatory or not?
     * @param _array array type?
     */
    public void add(final String _name, final Class<?> _clazz,
            final boolean _mandatory, final boolean _array) {

        ParameterDescription parameter = new ParameterDescription(_name,
                _clazz, _mandatory, _array);
        parameters.put(_name, parameter);
    }

    ParameterDescription get(final String _name) {
        return parameters.get(_name);
    }

    /**
     * Validates the {@link RequestAttributes}.
     *
     * @param _requestAttributes The attributes to validate.
     * @return Some messages.
     */
    public Messages validate(final RequestAttributes _requestAttributes) {
        final Messages messages = new Messages();

        for (Entry<String, ParameterDescription> parameterEntry : parameters
                .entrySet()) {

            ParameterDescription parameter = parameterEntry.getValue();
            String value = _requestAttributes.getValue(parameter.getName());

            if (parameter.isMandatory() && !parameter.isArray()) {

                if (StringUtils.isBlank(value)) {
                    messages.add(new Message(Type.MANDATORY_BUT_BLANK,
                            parameter.getName(), "It is a mandatory field!"));
                }
                
                if (StringUtils.containsAny(value, "<>\"")) {
                    messages.add(new Message(Type.ILLEGAL_CHARACTER,
                            parameter.getName(), "Contains illegal character!"));
                }

            } else if (parameter.isMandatory() && parameter.isArray()) {
                
                // TODO

            }
        }

        return messages;
    }

    /**
     * Describes the expected HTTP request parameters.
     *
     * @author by Andre Winkler, $LastChangedBy: andrewinkler $
     * @version $LastChangedRevision: 3563 $ $LastChangedDate: 2013-02-19 18:39:15 +0100 (Tue, 19 Feb 2013) $
     */
    public static class ParameterDescription {

        private final String name;
        private final Class<?> clazz;
        private final boolean mandatory;
        private final boolean array;

        ParameterDescription(final String _name, final Class<?> _clazz,
                final boolean _mandatory, final boolean _array) {

            name = _name;
            clazz = _clazz;
            mandatory = _mandatory;
            array = _array;
        }

        /**
         * @return the HTTP parameter name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the parameter type
         */
        public Class<?> getClazz() {
            return clazz;
        }

        /**
         * @return the mandatory or not?
         */
        public boolean isMandatory() {
            return mandatory;
        }

        /**
         * @return is it an array data type?
         */
        public boolean isArray() {
            return array;
        }

    }

}
