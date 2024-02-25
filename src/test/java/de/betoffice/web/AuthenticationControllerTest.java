package de.betoffice.web;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.betoffice.web.auth.AuthenticationController;
import de.betoffice.web.auth.AuthenticationForm;
import de.betoffice.web.auth.BetofficeAuthenticationService;
import de.betoffice.web.auth.LogoutFormData;
import de.betoffice.web.json.SecurityTokenJson;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@SpringJUnitConfig(locations = { "/betoffice-test-properties.xml", "/betoffice.xml" })
public class AuthenticationControllerTest {

    private static final String TOKEN = "TOKEN";
    private static final String NICKNAME = "Frosch";
    private static final String PASSWORD = "Password";
    private static final String ROLE_TIPPER = "TIPPER";
    private static final String USER_AGENT_TEST = "TESTAGENT";

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        BetofficeAuthenticationService betofficeAuthenticationService = Mockito.mock(BetofficeAuthenticationService.class);
        SecurityTokenJson token = new SecurityTokenJson();
        token.setToken(TOKEN);
        token.setNickname(NICKNAME);
        token.setRole(ROLE_TIPPER);
        when(betofficeAuthenticationService.login(eq("Frosch"), eq("Password"), anyString(), anyString(), anyString())).thenReturn(token);

        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthenticationController(betofficeAuthenticationService))
                .build();
    }

    @Test
    @Transactional
    public void loginLogout() throws Exception {
        //
        // Authentifizierung starten...
        //
        AuthenticationForm authenticationForm = new AuthenticationForm();
        authenticationForm.setNickname(NICKNAME);
        authenticationForm.setPassword(PASSWORD);

        /*ResultActions loginAction =*/ mockMvc.perform(post("/authentication/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toString(authenticationForm))
                .header("User-Agent", USER_AGENT_TEST)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("token", equalTo(TOKEN)))
                .andExpect(jsonPath("nickname", equalTo(NICKNAME)))
                .andExpect(jsonPath("role", equalTo(ROLE_TIPPER)));

        LogoutFormData logoutFormData = new LogoutFormData();
        logoutFormData.setNickname(NICKNAME);
        logoutFormData.setToken(TOKEN);

        /*ResultActions logoutAction =*/ mockMvc.perform(post("/authentication/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toString(logoutFormData))
                .header("User-Agent", USER_AGENT_TEST)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private String toString(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(object);
    }

}
