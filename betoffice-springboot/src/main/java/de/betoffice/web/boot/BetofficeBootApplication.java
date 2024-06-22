package de.betoffice.web.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = {"de.betoffice", "de.winkler.betoffice" })
public class BetofficeBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(BetofficeBootApplication.class, args);
    }

}
