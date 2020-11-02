package de.betoffice.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.betoffice.web.json.SubmitTippRoundJson;
import de.winkler.betoffice.service.MasterDataManagerService;
import de.winkler.betoffice.service.SeasonManagerService;
import de.winkler.betoffice.storage.GameList;
import de.winkler.betoffice.storage.Group;
import de.winkler.betoffice.storage.GroupType;
import de.winkler.betoffice.storage.Season;
import de.winkler.betoffice.storage.Team;
import de.winkler.betoffice.storage.User;
import de.winkler.betoffice.storage.enums.SeasonType;

@ExtendWith(SpringExtension.class)
// @ContextConfiguration(classes = { PersistenceJPAConfig.class })
// @ComponentScan("de.betoffice", "de.winkler.betoffice")
@WebAppConfiguration
@SpringJUnitConfig(locations = { "/betoffice-test-properties.xml", "/betoffice.xml" })
public class TippControllerTest {

    private static final ZoneId EUROPE_BERLIN = ZoneId.of("Europe/Berlin");
    private static final LocalDateTime LDT_1971_03_24 = LocalDateTime.of(1971, 3, 24, 20, 30, 0);
    private static final ZonedDateTime DATE_1971_03_24 = ZonedDateTime.of(LDT_1971_03_24, EUROPE_BERLIN);

    private MockMvc mockMvc;

    @Autowired
    private BetofficeService betofficeService;

    @Autowired
    private MasterDataManagerService masterDataManagerService;

    @Autowired
    private SeasonManagerService seasonManagerService;

    private GameList round;

    @Test
    @Transactional
    public void ping() throws Exception {
        mockMvc.perform(get("/office/ping")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        
        SubmitTippRoundJson tipp = new SubmitTippRoundJson();
        
        mockMvc.perform(
                post("/office/tipp/submit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toString(tipp))
                    .header(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN, "token")
                    .header(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME, "User A")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void emptyRegistration() throws Exception {
        // RegistrationJson registration = new RegistrationJson();
        //
        // String requestJson = toString(registration);
        //
        // mockMvc.perform(post("/registration/register")
        // .contentType(MediaType.APPLICATION_JSON)
        // .content(requestJson))
        // .andDo(print())
        // .andExpect(status().isOk())
        // .andExpect(jsonPath("validationCodes.*", Matchers.hasSize(7)))
        // .andExpect(jsonPath("validationCodes.*",
        // Matchers.containsInAnyOrder("UNKNOWN_APPLICATION",
        // "MISSING_ACCEPT_EMAIL", "MISSING_ACCEPT_COOKIE", "NICKNAME_IS_EMPTY",
        // "PASSWORD_TOO_SHORT",
        // "EMAIL_IS_EMPTY", "FIRSTNAME_IS_EMPTY")));
    }

    /**
     * Die Annotation {@code @Transactional} sorgt dafuer, dass die angelegten
     * Testdaten nach Testausfuehrung zurueck gerollt werden.
     * 
     * @throws Exception
     *             ...
     */
    @Test
    @Transactional
    public void validRegistration() throws Exception {
        // setupDatabase();
        // RegistrationJson registration = new RegistrationJson();
        // registration.setApplicationName("application");
        //
        // mockMvc.perform(post("/registration/register")
        // .contentType(MediaType.APPLICATION_JSON)
        // .content(toString(registration)))
        // .andDo(print())
        // .andExpect(status().isOk())
        // .andExpect(jsonPath("validationCodes.*", Matchers.hasSize(6)))
        // .andExpect(jsonPath("validationCodes.*",
        // Matchers.containsInAnyOrder("MISSING_ACCEPT_EMAIL", "MISSING_ACCEPT_COOKIE",
        // "NICKNAME_IS_EMPTY", "PASSWORD_TOO_SHORT", "EMAIL_IS_EMPTY",
        // "FIRSTNAME_IS_EMPTY")));
        //
        // registration.setAcceptCookie(true);
        // registration.setAcceptMail(true);
        // registration.setNickname("Frosch");
        // registration.setFirstname("Andre");
        // registration.setName("Winkler");
        // registration.setPassword("secret-password");
        // registration.setEmail("test@test.de");
        //
        // mockMvc.perform(post("/registration/register")
        // .contentType(MediaType.APPLICATION_JSON)
        // .content(toString(registration)))
        // .andDo(print())
        // .andExpect(status().isOk())
        // .andExpect(jsonPath("validationCodes", Matchers.hasSize(1)))
        // .andExpect(jsonPath("validationCodes", Matchers.contains("OK")));
        //
        // RegistrationEntity registrationEntity =
        // registrationRepository.findByNickname("Frosch")
        // .orElseThrow(() -> new IllegalArgumentException());
        // assertThat(registrationEntity.getNickname()).isEqualTo("Frosch");
        // assertThat(registrationEntity.getApplication()).isEqualTo("application");
    }

    /**
     * Die Annotation {@code @Transactional} sorgt dafuer, dass die angelegten
     * Testdaten nach Testausfuehrung zurueck gerollt werden.
     * 
     * @throws Exception
     *             ...
     */
    @Test
    @Transactional
    public void invalidEmailRegistration() throws Exception {
        // setupDatabase();
        // RegistrationJson registration = new RegistrationJson();
        // registration.setApplicationName("application");
        //
        // registration.setAcceptCookie(true);
        // registration.setAcceptMail(true);
        // registration.setNickname("Frosch");
        // registration.setFirstname("Andre");
        // registration.setName("Winkler");
        // registration.setPassword("secret-password");
        // registration.setEmail("test(at)test.de");
        //
        // mockMvc.perform(post("/registration/register")
        // .contentType(MediaType.APPLICATION_JSON)
        // .content(toString(registration)))
        // .andDo(print())
        // .andExpect(status().isOk())
        // .andExpect(jsonPath("validationCodes", Matchers.hasSize(1)))
        // .andExpect(jsonPath("validationCodes",
        // Matchers.contains("EMAIL_IS_NOT_VALID")));
    }

    private String toString(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(object);
    }

    // private void setupDatabase() {
    // ApplicationEntity application = new ApplicationEntity();
    // application.setName("application");
    // application.setDescription("Application Description");
    // applicationRepository.save(application);
    // }

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new BetofficeController(betofficeService))
                .build();

        Team luebeck = new Team("Vfb Lübeck", "Vfb Lübeck", "luebeck.gif");
        masterDataManagerService.createTeam(luebeck);
        Team rwe = new Team("RWE", "Rot-Weiss-Essen", "rwe.gif");
        masterDataManagerService.createTeam(rwe);

        Season season = new Season();
        season.setMode(SeasonType.LEAGUE);
        season.setName("Bundesliga");
        season.setYear("1999/2000");
        seasonManagerService.createSeason(season);

        GroupType buli1 = new GroupType();
        buli1.setName("1. Bundesliga");
        masterDataManagerService.createGroupType(buli1);

        season = seasonManagerService.addGroupType(season, buli1);
        Group group = seasonManagerService.findGroup(season, buli1);
        seasonManagerService.addTeam(season, buli1, rwe);
        seasonManagerService.addTeam(season, buli1, luebeck);

        round = seasonManagerService.addRound(season, DATE_1971_03_24, buli1);
        seasonManagerService.addMatch(round, DATE_1971_03_24, group, luebeck, rwe);
        seasonManagerService.addMatch(round, DATE_1971_03_24, group, rwe, luebeck);

        User userA = new User();
        userA.setNickname("User A");
        masterDataManagerService.createUser(userA);

        seasonManagerService.addUser(season, userA);
    }

}
