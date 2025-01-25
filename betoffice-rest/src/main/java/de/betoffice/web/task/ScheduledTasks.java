package de.betoffice.web.task;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.winkler.betoffice.service.CommunityService;
import de.winkler.betoffice.service.SeasonManagerService;
import de.winkler.betoffice.service.TippService;
import de.winkler.betoffice.storage.Community;
import de.winkler.betoffice.storage.GameList;
import de.winkler.betoffice.storage.Season;
import de.winkler.betoffice.storage.User;

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
        Season season = nextTippRound.get().getSeason();

        Set<User> members = communityService.findMembers(CommunityService.defaultPlayerGroup(season.getReference()));
        
        // List<User> users = findUsers(seasonMembers);
        // Season season = seasonManagerService.findSeasonById(seasonId);
        // CommunityReference defaultPlayerGroup = CommunityService.defaultPlayerGroup(season.getReference());
        // tippService.find
        System.out.println("Hallo. Hallo. Hallo");
    }

}
