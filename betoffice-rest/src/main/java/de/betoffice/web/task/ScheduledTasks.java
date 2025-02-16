package de.betoffice.web.task;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import de.betoffice.web.json.GameJson;
import de.betoffice.web.json.RoundJson;
import de.betoffice.web.tipp.OfficeTippService;
import de.winkler.betoffice.service.CommunityService;
import de.winkler.betoffice.service.TippService;
import de.winkler.betoffice.storage.GameList;
import de.winkler.betoffice.storage.Season;
import de.winkler.betoffice.storage.User;
import de.winkler.betoffice.storage.enums.NotificationType;

@Configuration
@EnableScheduling
public class ScheduledTasks {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);

    private final SendReminderMailNotification sendReminderMailNotification;

    public ScheduledTasks(SendReminderMailNotification sendReminderMailNotification) {
        this.sendReminderMailNotification = sendReminderMailNotification;   
    }

    // @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    @Scheduled(cron = "0 0 1 * * *")
    public void scheduler() {
        LOG.info("Start scheduler...");
        sendReminderMailNotification.send();
    }

}
