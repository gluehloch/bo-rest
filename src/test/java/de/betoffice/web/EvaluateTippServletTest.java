/*
 * ============================================================================
 * Project betoffice-jweb
 * Copyright (c) 2000-2016 by Andre Winkler. All rights reserved.
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

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * A test class for {@link TippServlet}. This test case needs a running
 * web server!
 * 
 * @author by Andre Winkler
 */
@Ignore
public class EvaluateTippServletTest {

    private static final String BASE_URL = "http://localhost:8080/betoffice-jweb";

    private static final String HOME_GOALS_INPUT_ID = "homeGoals";
    private static final String GUEST_GOALS_INPUT_ID = "guestGoals";

    private WebClient webClient;

    @Before
    public void setUp() {
        webClient = new WebClient();
        webClient.setAjaxController(new MyAjaxController());
    }

    @After
    public void tearDown() {
        webClient.closeAllWindows();
    }

    @Test
    public void testBetofficeHomeTitle() throws Exception {
        final HtmlPage page = webClient.getPage(BASE_URL);
        assertThat(page.getTitleText()).isEqualTo("Betoffice Web Application");

        final String pageAsXml = page.asXml();
        assertThat(pageAsXml.contains("<body>")).isTrue();

        final String pageAsText = page.asText();
        assertThat(pageAsText.contains("betoffice jweb")).isTrue();
    }

    @Test
    public void testAlertSetHomeAndGuestGoals() throws Exception {
        final List<String> collectedAlerts = new ArrayList<String>();
        webClient.setAlertHandler(new CollectingAlertHandler(collectedAlerts));
        final HtmlPage page = webClient.getPage(BASE_URL + "/test_tipp.html");

        assertThat(page.getTitleText()).isEqualTo("Tipp die Kiste Bier");
        HtmlForm htmlForm = page.getFormByName("tippform");
        HtmlInput userName = htmlForm.getInputByName("user_name");
        userName.setValueAttribute("Andre");
        HtmlInput password = htmlForm.getInputByName("pwd");
        password.setValueAttribute("myPassword");

        final HtmlButtonInput button = htmlForm.getInputByName("send");
        button.click();

        final List<String> expectedAlerts = Collections
                .singletonList("Alle Heim- und Gasttore richtig setzen!");
        assertThat(collectedAlerts).isEqualTo(expectedAlerts);
    }

    @Test
    public void testSubmitTipp() throws Exception {
        final HtmlPage page = webClient.getPage(BASE_URL + "/test_tipp.html");

        assertThat(page.getTitleText()).isEqualTo("Tipp die Kiste Bier");
        HtmlForm htmlForm = page.getFormByName("tippform");
        HtmlInput userName = htmlForm.getInputByName("user_name");
        userName.setValueAttribute("Andre");
        HtmlInput password = htmlForm.getInputByName("pwd");
        password.setValueAttribute("myPassword");

        final int matchCount = 10;
        int goal = 0;
        for (int match = 1; match < matchCount; match++) {
            HtmlInput homeGoals = htmlForm.getInputByName(HOME_GOALS_INPUT_ID
                    + match);
            HtmlInput guestGoals = htmlForm.getInputByName(GUEST_GOALS_INPUT_ID
                    + match);
            homeGoals.setValueAttribute(Integer.toString(goal++));
            guestGoals.setValueAttribute(Integer.toString(goal++));
        }

        final HtmlButtonInput button = htmlForm.getInputByName("send");
        button.click();

        Thread.sleep(100);

        // TODO Momentan scheint hier gar nichts angezeigt zu werden...

        final HtmlDivision okDiv = (HtmlDivision) page.getByXPath(
                "//div[@id='ok']").get(0);
        assertThat(okDiv.isDisplayed()).isFalse();

        final HtmlDivision errorDiv = (HtmlDivision) page.getByXPath(
                "//div[@id='error']").get(0);
        assertThat(errorDiv.isDisplayed()).isFalse();

        final HtmlDivision waitingDiv = (HtmlDivision) page.getByXPath(
                "//div[@id='waiting']").get(0);
        // The div was enabled at the beginning, but disabled after processing.
        assertThat(waitingDiv.isDisplayed()).isFalse();

        final HtmlDivision fatalErrorDiv = (HtmlDivision) page.getByXPath(
                "//div[@id='fatalerror']").get(0);
        assertThat(fatalErrorDiv.isDisplayed()).isFalse();
    }

    private class MyAjaxController extends AjaxController {

        private static final long serialVersionUID = -2870428017732339145L;

        @Override
        public boolean processSynchron(final HtmlPage page,
                final WebRequest request, final boolean async) {

            return true;
        }

    }

}
