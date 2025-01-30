package de.betoffice.web.task;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

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

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm");

    private final OfficeTippService officeTippService;
    private final TippService tippService;
    private final CommunityService communityService;
    private final MailTask mailTask;

    public ScheduledTasks(
            final OfficeTippService officeTippService,
            final TippService tippService,
            final CommunityService communityService,
            final MailTask mailTask) {
        this.officeTippService = officeTippService;
        this.tippService = tippService;
        this.communityService = communityService;
        this.mailTask = mailTask;
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    public void scheduler() {
        LOG.info("Start scheduler...");

        // TODO Wie stelle ich sicher, dass die Mail für einen Spieltag nur einmal rausgeschickt wird?
        // Der Job läuft nur einmal am Tag. Falls Spiele an diesem Tag sind, wird eine Mail verschickt.
        // Ist ein Spieltag über mehrere Tage verteilt, bekommt man dann für jeden Tag eine Email.

        Optional<GameList> nextTippRound = tippService.findNextTippRound(ZonedDateTime.now());
        Season season = nextTippRound.get().getSeason();

        Set<User> members = communityService.findMembers(CommunityService.defaultPlayerGroup(season.getReference()));
        members.stream().filter(ScheduledTasks::notify).forEach(u -> {
            try {
                RoundJson roundJson = officeTippService.findTipp(nextTippRound.get().getId(),
                        u.getNickname().getNickname());

                StringBuilder sb = new StringBuilder();
                sb.append("Heute ist Spieltag. Vergiss deinen Tipp nicht: https://tippdiekistebier.de\n");
                for (var game : roundJson.getGames()) {
                    sb.append("\n").append(game.getDateTime().format(formatter)).append(" ");
                    sb.append(game.getHomeTeam().getName()).append(" - ").append(game.getGuestTeam().getName());
                }

                // roundJson.getGames().get(0).getTipps();

                mailTask.send("betoffice@andre-winkler.de", u.getEmail(), "Spieltag!", sb.toString());
            } catch (Exception ex) {
                LOG.error(String.format("Unable to send an email to %s", u.getEmail()), ex);
            }
        });
    }

    public static boolean notify(User user) {
        return NotificationType.TIPP.equals(user.getNotification());
    }

}
