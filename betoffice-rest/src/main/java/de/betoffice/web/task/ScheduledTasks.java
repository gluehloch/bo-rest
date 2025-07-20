/*
 * ============================================================================
 * Project betoffice-jweb Copyright (c) 2015-2025 by Andre Winkler. All rights
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

package de.betoffice.web.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import de.winkler.betoffice.mail.SendReminderMailNotification;

@Configuration
@EnableScheduling
public class ScheduledTasks {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);

    private final SendReminderMailNotification sendReminderMailNotification;

    public ScheduledTasks(SendReminderMailNotification sendReminderMailNotification) {
        this.sendReminderMailNotification = sendReminderMailNotification;
    }

    // @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS) // Local test setup
    @Scheduled(cron = "0 0 1 * * *") // production setup. Should be configured.
    public void scheduler() {
        LOG.info("Start scheduler...");
        sendReminderMailNotification.send();
    }

}
