/*
 * $Id: EvaluateTippServlet.java 3926 2014-03-08 09:58:34Z andrewinkler $
 * ============================================================================
 * Project betoffice-jweb
 * Copyright (c) 2000-2013 by Andre Winkler. All rights reserved.
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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.betoffice.web.http.ResponseHeaderSetup;
import de.betoffice.web.validator.EvaluateTippAuthRequestValidator;
import de.betoffice.web.validator.EvaluateTippRequestValidator;
import de.betoffice.web.validator.JWebConst;
import de.betoffice.web.validator.Message;
import de.betoffice.web.validator.Message.Type;
import de.betoffice.web.validator.Messages;
import de.betoffice.web.validator.RequestAttributes;
import de.betoffice.web.validator.RequestValidator;
import de.winkler.betoffice.mail.TippMailParameter;
import de.winkler.betoffice.service.AuthService;
import de.winkler.betoffice.service.SecurityToken;
import de.winkler.betoffice.service.TippService;
import de.winkler.betoffice.storage.GameList;
import de.winkler.betoffice.storage.User;

/**
 * Evaluate and authentice a tipp form.
 * 
 * @author by Andre Winkler, $LastChangedBy: andrewinkler $
 * @version $LastChangedRevision: 2808 $ $LastChangedDate: 2011-02-16 18:27:12 +0100 (Mi, 16 Feb 2011) $
 */
@Controller
@RequestMapping("/tipp")
public class EvaluateTippServlet {

	private static final Logger log = LoggerFactory
			.getLogger(EvaluateTippServlet.class);

	private AuthService authService;

	@Autowired
	public void setAuthService(AuthService _authService) {
		authService = _authService;
	}

	private TippService tippService;

	@Autowired
	public void setTippService(TippService _tippService) {
		tippService = _tippService;
	}

	// -----------------------------------------------------------------------

	@RequestMapping(value = "/auth", method = RequestMethod.POST)
	public @ResponseBody
	Messages auth(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	    ResponseHeaderSetup.setup(response);

		RequestAttributes requestAttributes = new RequestAttributes(
				request.getParameterMap());
		EvaluateTippAuthRequestValidator evaluateTippRequestValidator = new EvaluateTippAuthRequestValidator();

		RequestValidator requestValidator = evaluateTippRequestValidator
				.createRequestValidator();

		Messages messages = requestValidator.validate(requestAttributes);

		if (messages.isOk()) {

			try {
				AuthFormData authFormData = new AuthFormDataConverter()
						.convert(requestAttributes);

				SecurityToken securityToken = authService.login(
						authFormData.getNickname(), authFormData.getPassword());

				HttpSession session = request.getSession();
				session.setAttribute(SecurityToken.class.getName(),
						securityToken);

				if (securityToken.isNotLoggedIn()) {

					messages.add(new Message(Type.LOGIN_INVALID, "nickname",
							"Login ist fehlgeschlagen!"));

				} else {

					messages.add(new Message(Type.LOGGED_IN, "nickname",
							"Login war erfolgreich!"));

				}
			} catch (TippRequestInvalidException ex) {
				messages.add(new Message(Type.ERROR, "auth",
						"Die Benutzereingaben konnten nicht verarbeitet werden!"));
			}

		} else {

			messages.add(new Message(Type.ERROR, "nickname",
					"Die Benutzereingaben konnten nicht verarbeitet werden!"));

		}

		return messages;
	}

	/**
	 * The complete URL for this service looks like this:
	 * <code>&lt;context&gt;/bo/tipp/receive</code>
	 * 
	 * All betoffice tipp forms use this service for tipp evaluation.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/receive/{round}/{nickname}", method = RequestMethod.GET)
	public @ResponseBody
	String receive(@PathVariable("nickname") String nickname,
			@PathVariable("round") int round, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ResponseHeaderSetup.setup(response);

		// TODO
		User user = null;
		GameList gameList = null;

		// authService.login(name, password)
		//
		// List<GameTipp> findTippsByRoundAndUser =
		// tippService.findTippsByRoundAndUser(gameList, user);

		return "4711";
	}

	/**
	 * The complete URL for this service looks like this:
	 * <code>&lt;context&gt;/bo/tipp/submit</code>
	 * 
	 * All betoffice tipp forms use this service for tipp evaluation.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public @ResponseBody
	Messages submit(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Allows requests/response for other origin. Should used with care.
		// If not set, the caller gets an emtpy response.

	    ResponseHeaderSetup.setup(response);

		RequestAttributes requestAttributes = new RequestAttributes(
				request.getParameterMap());
		EvaluateTippRequestValidator evaluateTippRequestValidator = new EvaluateTippRequestValidator();

		RequestValidator requestValidator = evaluateTippRequestValidator
				.createRequestValidator();

		Messages messages = requestValidator.validate(requestAttributes);
		request.setAttribute("messages", messages);

		if (messages.isOk()) {

			try {
				TippFormData tippFormData = new TippFormDataConverter()
						.convert(requestAttributes);

				SecurityToken securityToken = authService.login(
						tippFormData.getNickname(), tippFormData.getPassword());

				// HttpSession session = request.getSession(false);
				// SecurityToken securityToken = null;
				// if (session == null) {
				// securityToken = new SecurityToken("-1", false, null);
				// } else {
				// securityToken = (SecurityToken) session
				// .getAttribute(SecurityToken.class.getName());
				// }

				if (securityToken.isNotLoggedIn()) {

					messages.add(new Message(Type.NOT_LOGGED_IN,
							JWebConst.F_NICKNAME, "Du bist nicht eingeloggt!"));

				} else {

					TippMailParameter params = new TippMailParameter(
							securityToken.getUser().getNickName(),
							securityToken.getUser().getPassword(),
							tippFormData.getHomeGoals(),
							tippFormData.getGuestGoals(),
							tippFormData.getHomeTeams(),
							tippFormData.getGuestTeams(),
							tippFormData.getRoundNr(),
							tippFormData.getChampionship(), "localhost",
							request.getRemoteAddr(),
							request.getHeader("User-Agent"));

					try {
						tippService.sendMailTipp(securityToken.getUser(),
								params);
					} catch (RuntimeException ex) {
						log.error("Tippauswertung fehlgeschlagen!", ex);
						messages.add(new Message(
								Type.MAILILNG_FAILED,
								"tipp",
								"Technische Probleme verhindern den Mail-Versand. Versuche es später noch einmal."));
					}
				}

			} catch (TippRequestInvalidException ex) {
				messages.add(new Message(Type.ERROR, "tipp",
						"Die Benutzereingaben konnten nicht verarbeitet werden!"));
			}

		} else {

			messages.add(new Message(Type.ERROR, "tipp",
					"Die Benutzereingaben konnten nicht verarbeitet werden!"));

		}

		return messages;
	}

}
