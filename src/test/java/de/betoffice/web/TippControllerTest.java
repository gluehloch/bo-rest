/*
 * ============================================================================
 * Project betoffice-jweb
 * Copyright (c) 2000-2020 by Andre Winkler. All rights reserved.
 * ============================================================================
 *          GNU GENERAL PUBLIC LICENSE
 *  TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package de.betoffice.web;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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

import de.betoffice.web.json.GameResultJson;
import de.betoffice.web.json.SubmitTippGameJson;
import de.betoffice.web.json.SubmitTippRoundJson;
import de.winkler.betoffice.dao.SessionDao;
import de.winkler.betoffice.service.MasterDataManagerService;
import de.winkler.betoffice.service.SeasonManagerService;
import de.winkler.betoffice.storage.Game;
import de.winkler.betoffice.storage.GameList;
import de.winkler.betoffice.storage.GameTipp;
import de.winkler.betoffice.storage.Group;
import de.winkler.betoffice.storage.GroupType;
import de.winkler.betoffice.storage.Season;
import de.winkler.betoffice.storage.Session;
import de.winkler.betoffice.storage.Team;
import de.winkler.betoffice.storage.User;
import de.winkler.betoffice.storage.enums.SeasonType;

/**
 * Die Spring-Security Konfiguration kann auf diese Art und Weise nicht mit getestet werden.
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@SpringJUnitConfig(locations = { "/betoffice-test-properties.xml", "/betoffice.xml" })
public class TippControllerTest {

    private static final String NICKNAME = "Frosch";
    private static final String PASSWORD = "Password";
    private static final String USER_AGENT_TEST = "TESTAGENT";
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

    private T data;

    @Test
    @Transactional
    public void ping() throws Exception {
        mockMvc.perform(get("/office/ping")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void submitValidTipp() throws Exception {
        //
        // Authentifizierung starten...
        //
        AuthenticationForm authenticationForm = new AuthenticationForm();
        authenticationForm.setNickname(NICKNAME);
        authenticationForm.setPassword(PASSWORD);

        ResultActions loginAction = mockMvc.perform(post("/office/login")
                //.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE) /* APPLICATION_JSON */
                .content(toString(authenticationForm))
                .header("Authorization", "Bearer ")
                //.header("User-Agent", USER_AGENT_TEST)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("token", equalTo("1")))
                .andExpect(jsonPath("nickname", equalTo(NICKNAME)))
                .andExpect(jsonPath("role", equalTo("TIPPER")));

        List<Session> sessions = sessionDao.findByNickname(NICKNAME);
        assertThat(sessions).hasSize(1);
        String token = sessions.get(0).getToken();
        
        LogoutFormData logoutFormData = new LogoutFormData();
        logoutFormData.setNickname(NICKNAME);
        logoutFormData.setToken(token);
        
        ResultActions logoutAction = mockMvc.perform(post("/office/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toString(logoutFormData))
                .header("User-Agent", USER_AGENT_TEST)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        
        List<Session> sessions2 = sessionDao.findByNickname(NICKNAME);
        assertThat(sessions2).hasSize(1);
        ZonedDateTime logout = sessions2.get(0).getLogout();
        assertThat(logout).isNotNull();
    }

    @Test
    @Transactional
    public void submitInvalidTipp() throws Exception {
        //
        // Versuch der Tippabgabe ohne Authentifizierung.
        //
        SubmitTippRoundJson tippWithoutAuthentication = new SubmitTippRoundJson();

        mockMvc.perform(post("/office/tipp/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toString(tippWithoutAuthentication))
                .header(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN, "token")
                .header(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME, NICKNAME)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());

        //
        // Authentifizierung starten...
        //
        AuthenticationForm authenticationForm = new AuthenticationForm();
        authenticationForm.setNickname(NICKNAME);
        authenticationForm.setPassword(PASSWORD);

        ResultActions loginAction = mockMvc.perform(post("/office/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toString(authenticationForm))
                .header("User-Agent", USER_AGENT_TEST)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("token", notNullValue()))
                .andExpect(jsonPath("nickname", equalTo(NICKNAME)))
                .andExpect(jsonPath("role", equalTo("TIPPER")));

        MvcResult result = loginAction.andReturn();
        System.out.println(result.getResponse().getContentAsString());

        List<Session> session = sessionDao.findByNickname(NICKNAME);
        assertThat(session).hasSize(1);
        assertThat(session.get(0).getBrowser()).isEqualTo(USER_AGENT_TEST);
        String token = session.get(0).getToken();

        //
        // Tippabgabe erfolgt (weit) nach Spielstart (also heute). Es wird ein leerer
        // Tipp mit zurueck gegeben (Der Nickname wird vom Server NICHT gesetzt!).
        //
        List<GameTipp> expectedTipps = seasonManagerService.findTipps(data.round, data.user);
        assertThat(expectedTipps).hasSize(0);

        SubmitTippRoundJson tipp = new SubmitTippRoundJson();
        tipp.setNickname(NICKNAME);
        tipp.setRoundId(data.round.getId());
        List<SubmitTippGameJson> submitTippGames = new ArrayList<>();
        SubmitTippGameJson submitTippGame = new SubmitTippGameJson();
        submitTippGame.setGameId(data.round.get(0).getId());
        GameResultJson gameResultJson = new GameResultJson();
        gameResultJson.setHomeGoals(2);
        gameResultJson.setGuestGoals(3);
        submitTippGame.setTippResult(gameResultJson);
        submitTippGames.add(submitTippGame);
        tipp.setSubmitTippGames(submitTippGames);

        ResultActions submitAction = mockMvc.perform(post("/office/tipp/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toString(tipp))
                .header(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_TOKEN, token)
                .header(BetofficeHttpConsts.HTTP_HEADER_BETOFFICE_NICKNAME, NICKNAME)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("seasonName", equalTo("Bundesliga")))
                .andExpect(jsonPath("seasonYear", equalTo("1999/2000")))
                .andExpect(jsonPath("games[0].homeTeam.name", equalTo("Vfb Lübeck")))
                .andExpect(jsonPath("games[0].guestTeam.name", equalTo("RWE")))
                .andExpect(jsonPath("games[0].tipps[0].nickname", nullValue()));

        System.out.println(submitAction.andReturn().getResponse().getContentAsString());

        seasonManagerService.findTippsByMatch(data.luebeckVsRwe);

        List<GameTipp> tipps = seasonManagerService.findTipps(data.round, data.user);
        assertThat(tipps).hasSize(0);

        //
        // Tippabgabe zum richtigen Zeitpunkt. Einen Tag vor dem Spieltag.
        //

        // TODO Wie stelle ich die Uhrzeit im Server fuer den Test passend ein?
        
//        assertThat(tipps.get(0).getToken()).isEqualTo("1");
//        assertThat(tipps.get(0).getUser().getNickname()).isEqualTo(NICKNAME);
//        assertThat(tipps.get(0).getTipp().getHomeGoals()).isEqualTo(2);
//        assertThat(tipps.get(0).getTipp().getGuestGoals()).isEqualTo(3);
    }

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

        data = new T();

        data.luebeck = new Team("Vfb Lübeck", "Vfb Lübeck", "luebeck.gif");
        masterDataManagerService.createTeam(data.luebeck);
        data.rwe = new Team("RWE", "Rot-Weiss-Essen", "rwe.gif");
        masterDataManagerService.createTeam(data.rwe);

        data.season = new Season();
        data.season.setMode(SeasonType.LEAGUE);
        data.season.setName("Bundesliga");
        data.season.setYear("1999/2000");
        seasonManagerService.createSeason(data.season);

        data.bundesliga = new GroupType();
        data.bundesliga.setName("1. Bundesliga");
        masterDataManagerService.createGroupType(data.bundesliga);

        data.season = seasonManagerService.addGroupType(data.season, data.bundesliga);
        data.group = seasonManagerService.findGroup(data.season, data.bundesliga);
        seasonManagerService.addTeam(data.season, data.bundesliga, data.rwe);
        seasonManagerService.addTeam(data.season, data.bundesliga, data.luebeck);

        data.round = seasonManagerService.addRound(data.season, DATE_1971_03_24, data.bundesliga);
        data.luebeckVsRwe = seasonManagerService.addMatch(data.round, DATE_1971_03_24, data.group, data.luebeck, data.rwe);
        data.rweVsLuebeck = seasonManagerService.addMatch(data.round, DATE_1971_03_24, data.group, data.rwe, data.luebeck);

        data.user = new User();
        data.user.setNickname(NICKNAME);
        data.user.setPassword(PASSWORD);
        masterDataManagerService.createUser(data.user);

        seasonManagerService.addUser(data.season, data.user);
    }

    private static class T {
        Season season;
        User user;
        Team luebeck;
        Team rwe;
        GroupType bundesliga;
        Group group;
        GameList round;
        Game luebeckVsRwe;
        Game rweVsLuebeck;
    }

}
