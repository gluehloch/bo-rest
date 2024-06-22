/*
 * ============================================================================
 * Project betoffice-jweb-misc Copyright (c) 2013-2017 by Andre Winkler. All rights
 * reserved.
 * ============================================================================
 * GNU GENERAL PUBLIC LICENSE TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND
 * MODIFICATION
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package de.betoffice.web.misc;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

/**
 * Date and Time utilities.
 *
 * @author Andre Winkler
 */
public class DateTimeUtils {

    // private static DateTimeFormatter DEFAULT_FORMATTER_FOR_JODA_DATETIME =
    // new DateTimeFormatterBuilder()
    // .appendYear(4, 4).appendLiteral("-").appendMonthOfYear(2)
    // .appendLiteral("-").appendDayOfMonth(2).appendLiteral(" ")
    // .appendHourOfDay(2).appendLiteral(":").appendMinuteOfHour(2)
    // .appendLiteral(":").appendSecondOfMinute(2).toFormatter();

    private static DateTimeFormatter DEFAULT_FORMATTER_ONLY_DATE = new DateTimeFormatterBuilder()
            .appendDayOfMonth(2).appendLiteral(".").appendMonthOfYear(2)
            .appendLiteral(".").appendYear(4, 4).toFormatter();

    private static DateTimeFormatter DEFAULT_FORMATTER_FOR_JODA_DATETIME = new DateTimeFormatterBuilder()
            .appendDayOfMonth(2).appendLiteral(".").appendMonthOfYear(2)
            .appendLiteral(".").appendYear(4, 4).appendLiteral(" ")
            .appendHourOfDay(2).appendLiteral(":").appendMinuteOfHour(2)
            .toFormatter();

    public static String onlyDate(DateTime dateTime) {
        return DEFAULT_FORMATTER_ONLY_DATE.print(dateTime);
    }

    public static String onlyDate(Date dateTime) {
        return DEFAULT_FORMATTER_ONLY_DATE.print(dateTime.getTime());
    }

    public static String toDateTime(DateTime dateTime) {
        return DEFAULT_FORMATTER_FOR_JODA_DATETIME.print(dateTime);
    }

    public static String toDateTime(Date dateTime) {
        return toDateTime(new DateTime(dateTime.getTime()));
    }

}
