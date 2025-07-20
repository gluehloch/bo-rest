package de.betoffice.web.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = { "de.betoffice", "de.winkler.betoffice" })
public class BetofficeBootApplication {

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        SpringApplication.run(BetofficeBootApplication.class, args);
    }

    @PostConstruct
    public void onStartup() {
        final var profiles = env.getActiveProfiles();
        System.out.println(String.format("Activated spring profile: %s", profilesToString(profiles)));
    }

    private String profilesToString(String[] profiles) {
        StringBuilder sb = new StringBuilder();
        for (String p : profiles) {
            sb.append(p).append(",");
        }
        return sb.toString();
    }
}
