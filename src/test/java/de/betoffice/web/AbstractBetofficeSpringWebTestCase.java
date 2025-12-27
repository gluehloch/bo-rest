package de.betoffice.web;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import de.betoffice.conf.PersistenceJPAConfiguration;
import de.betoffice.conf.TestPropertiesConfiguration;

@WebAppConfiguration
@ActiveProfiles(profiles = "test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { PersistenceJPAConfiguration.class, TestPropertiesConfiguration.class })
@ComponentScan({ "de.betoffice" })
public abstract class AbstractBetofficeSpringWebTestCase {

}
