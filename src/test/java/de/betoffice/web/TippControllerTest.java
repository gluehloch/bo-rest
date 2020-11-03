package de.betoffice.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.betoffice.web.json.SubmitTippRoundJson;
import de.winkler.betoffice.dao.SessionDao;
import de.winkler.betoffice.service.MasterDataManagerService;
import de.winkler.betoffice.service.SeasonManagerService;
import de.winkler.betoffice.storage.GameList;
import de.winkler.betoffice.storage.Group;
import de.winkler.betoffice.storage.GroupType;
import de.winkler.betoffice.storage.Season;
import de.winkler.betoffice.storage.Session;
import de.winkler.betoffice.storage.Team;
import de.winkler.betoffice.storage.User;
import de.winkler.betoffice.storage.enums.SeasonType;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@SpringJUnitConfig(locations = { "/betoffice-test-properties.xml", "/betoffice.xml" })
public class TippControllerTest {

    private static final String NICKNAME = "Frosch";
    private static final String PASSWORD = "Password";
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

    @Autowired
    private SessionDao sessionDao;
    
    private GameList round;

    @Test
    @Transactional
    public void submitTipp() throws Exception {
        mockMvc.perform(get("/office/ping")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        SubmitTippRoundJson tipp = new SubmitTippRoundJson();

        mockMvc.perform(post("/office/tipp/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toString(tipp))
                .header(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN, "token")
                .header(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME, "User A")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());

        AuthenticationForm authenticationForm = new AuthenticationForm();
        authenticationForm.setNickname(NICKNAME);
        authenticationForm.setPassword(PASSWORD);
        
        ResultActions loginAction = mockMvc.perform(post("/office/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toString(authenticationForm))
                .header("User-Agent", "TESTAGENT")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
                //.andExpect(header().string("User-Agent", "TESTAGENT"));

        MvcResult result = loginAction.andReturn();
        System.out.println(result.getResponse().getContentAsString());
        
        List<Session> session = sessionDao.findByNickname(NICKNAME);
        assertThat(session).hasSize(1);
        assertThat(session.get(0).getBrowser()).isEqualTo("TESTAGENT");
        
//        // Get the JWT from the response header ...
//        String authorizationHeader = loginAction.getResponse().getHeader(SecurityConstants.HEADER_STRING);
//        String jwt = authorizationHeader.replace(SecurityConstants.TOKEN_PREFIX, "");
//        // ... and validate the token.
//        Optional<String> validate = loginService.validate(jwt);
//        assertThat(validate).isPresent().get().isEqualTo("Frosch");
    }

    // @Test
    // @Transactional
    // public void validRegistration() throws Exception {
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
    // }

    private String toString(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(object);
    }

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
        userA.setNickname(NICKNAME);
        userA.setPassword(PASSWORD);
        masterDataManagerService.createUser(userA);

        seasonManagerService.addUser(season, userA);
    }

}
