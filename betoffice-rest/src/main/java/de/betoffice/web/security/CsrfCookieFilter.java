package de.betoffice.web.security;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CsrfCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (token != null) {
            Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
            String tokenValue = token.getToken();
            if (cookie == null || tokenValue != null && !tokenValue.equals(cookie.getValue())) {
                Cookie newCookie = new Cookie("XSRF-TOKEN", tokenValue);
                newCookie.setPath("/");
                newCookie.setHttpOnly(false);
                response.addCookie(newCookie);
            }
        }
        filterChain.doFilter(request, response);
    }
}
