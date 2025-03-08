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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.winkler.betoffice.storage.User;

@Service
public class SendUserProfileChangeMailNotification {

    private static final Logger LOG = LoggerFactory.getLogger(SendUserProfileChangeMailNotification.class);

    private final MailTask mailTask;
    private final String confirmMailAddressUrl;

    public SendUserProfileChangeMailNotification(
            final MailTask mailTask,
            @Value("${betoffice.mail.confirm.url}") final String confirmMailAddressUrl) {
        this.mailTask = mailTask;
        this.confirmMailAddressUrl = confirmMailAddressUrl;
    }

    /**
     * Versendet eine Bestätigungsmail an die gewünschte neue Email Adresse des Nutzers.
     * 
     * @param user The user
     */
    public void send(User user) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Hallo ")
                .append(user.getNickname().value()).append("!")
                .append(" Du möchtest deine Mail Adresse ändern? Dann bitte bestätige deine neue Mail-Adresse über den folgenden Link: ")
                .append(confirmMailAddressUrl).append("/")
                .append(user.getChangeToken());

        // https://tippdiekistebier.de/betoffice-war/office/profile/confirm-update

        try {
            mailTask.send("betoffice@andre-winkler.de", user.getChangeEmail(), "Mail Adresse wurde geändert",
                    sb.toString());
        } catch (Exception ex) {
            LOG.error(String.format("Unable to send an email to %s", user.getChangeEmail()), ex);
        }
    }

}
