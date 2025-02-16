package de.betoffice.web.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

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
