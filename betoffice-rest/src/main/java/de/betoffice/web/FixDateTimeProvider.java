/*
 * ============================================================================
 * Project betoffice-jweb
 * Copyright (c) 2000-2016 by Andre Winkler. All rights reserved.
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import de.winkler.betoffice.service.DateTimeProvider;

/**
 * 
 * @author Andre Winkler
 */
//@Component("dataTimeProvider")
public class FixDateTimeProvider implements DateTimeProvider {

    /**
     * Kurz vor dem letzten Spieltag der Bundesliga 2016/2017.
     */
    @Override
    public ZonedDateTime currentDateTime() {
        return ZonedDateTime.of(LocalDateTime.of(2020, 06, 05, 12, 00), ZoneId.of("Europe/Berlin"));
    }

    @Override
    public ZoneId defaultZoneId() {
        return ZoneId.of("Europe/Berlin");
    }

}
