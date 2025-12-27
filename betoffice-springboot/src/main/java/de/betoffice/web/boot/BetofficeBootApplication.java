package de.betoffice.web.boot;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = { "de.betoffice" })
public class BetofficeBootApplication extends SpringBootServletInitializer {

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        // Preferred (Java 8+)
        ZoneId zoneId = ZoneId.systemDefault();
        System.out.println("ZoneId.systemDefault(): " + zoneId);

        // java.util.TimeZone
        TimeZone tz = TimeZone.getDefault();
        System.out.println("TimeZone.getDefault().getID(): " + tz.getID());

        // System property (may be null or not set)
        String prop = System.getProperty("user.timezone");
        System.out.println("System.getProperty(\"user.timezone\"): " + prop);

        // Current offset for the runtime timezone
        ZoneOffset offset = ZonedDateTime.now(zoneId).getOffset();
        System.out.println("Current offset: " + offset);

        SpringApplication.run(BetofficeBootApplication.class, args);
    }

    @PostConstruct
    public void onStartup() {
        final var profiles = env.getActiveProfiles();
        System.out.println(String.format("Activated spring profile: %s", profilesToString(profiles)));
    }

    private String profilesToString(String[] profiles) {
        return String.join(",", profiles);
    }

}
