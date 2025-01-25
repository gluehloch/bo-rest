package de.betoffice.web.task;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

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

    private final TippService tippService;
    private final CommunityService communityService;
    private final MailTask mailTask;

    public ScheduledTasks(
            final TippService tippService,
            final CommunityService communityService,
            final MailTask mailTask) {
        this.tippService = tippService;
        this.communityService = communityService;
        this.mailTask = mailTask;
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    public void scheduler() {
        Optional<GameList> nextTippRound = tippService.findNextTippRound(ZonedDateTime.now());
        Season season = nextTippRound.get().getSeason();

        Set<User> members = communityService.findMembers(CommunityService.defaultPlayerGroup(season.getReference()));
        members.stream().filter(ScheduledTasks::notify).forEach(u -> {
            try {
                mailTask.send("betoffice@andre-winkler.de", u.getEmail(), "Spieltag!",
                        "Heute ist Spieltag. Vergiss deinen Tipp nicht: https://andre-winkler.de");
            } catch (Exception ex) {
                LOG.error(String.format("Unable to send an email to %s", u.getEmail()), ex);
            }
        });

        System.out.println("Hallo. Hallo. Hallo");
    }

    public static boolean notify(User user) {
        return NotificationType.TIPP.equals(user.getNotification());
    }

}
