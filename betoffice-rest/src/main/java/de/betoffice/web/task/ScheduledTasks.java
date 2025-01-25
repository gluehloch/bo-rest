package de.betoffice.web.task;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.winkler.betoffice.service.CommunityService;
import de.winkler.betoffice.service.SeasonManagerService;
import de.winkler.betoffice.service.TippService;
import de.winkler.betoffice.storage.Community;
import de.winkler.betoffice.storage.GameList;

@Configuration
@EnableScheduling
public class ScheduledTasks {

    private final TippService tippService;
    private final CommunityService communityService;
    private final SeasonManagerService seasonManagerService;

    public ScheduledTasks(
            final TippService tippService,
            final CommunityService communityService,
            final SeasonManagerService seasonManagerService) {
        this.tippService = tippService;
        this.communityService = communityService;
        this.seasonManagerService = seasonManagerService;
    }

    //@Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void scheduler() {
        Optional<GameList> nextTippRound = tippService.findNextTippRound(ZonedDateTime.now());

        List<Community> list = communityService.find(CommunityService.DEFAULT_PLAYER_GROUP);
        // List<User> users = findUsers(seasonMembers);
        // Season season = seasonManagerService.findSeasonById(seasonId);
        // CommunityReference defaultPlayerGroup = CommunityService.defaultPlayerGroup(season.getReference());
        // tippService.find
        System.out.println("Hallo. Hallo. Hallo");
    }

}
