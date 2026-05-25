/*
 * ============================================================================
 * Project betoffice-jweb
 * Copyright (c) 2000-2023 by Andre Winkler. All rights reserved.
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

package de.betoffice.web.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import de.betoffice.service.AuthService;
import de.betoffice.storage.user.RoleType;
import de.betoffice.storage.user.UserDao;
import de.betoffice.storage.user.entity.Nickname;
import de.betoffice.storage.user.entity.User;
import de.betoffice.web.BetofficeUrlPath;

/**
 * Security configuration.
 *
 * @author by Andre Winkler
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new BetofficeUserAccountDetailsService(userDao);
    }

    // Spring Security 7: build a ProviderManager from the configured AuthenticationProvider(s)
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(authenticationProvider));
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http, AuthenticationManager authenticationManager)
            throws Exception {

        HeaderWriterLogoutHandler clearSiteData = new HeaderWriterLogoutHandler(
                new ClearSiteDataHeaderWriter(Directive.ALL));

        http
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authz -> authz
                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                .requestMatchers(HttpMethod.GET, BetofficeUrlPath.URL_OFFICE + "/**").permitAll()
                // Authentication
                .requestMatchers(HttpMethod.GET,
                        BetofficeUrlPath.URL_AUTHENTICATION + BetofficeUrlPath.URL_AUTHENTICATION_PING)
                .permitAll()
                .requestMatchers(HttpMethod.POST,
                        BetofficeUrlPath.URL_AUTHENTICATION + BetofficeUrlPath.URL_AUTHENTICATION_LOGIN)
                .permitAll()
                .requestMatchers(HttpMethod.POST,
                        BetofficeUrlPath.URL_AUTHENTICATION + BetofficeUrlPath.URL_AUTHENTICATION_LOGOUT)
                .authenticated()

                // Send tipp form
                .requestMatchers(HttpMethod.POST, BetofficeUrlPath.URL_OFFICE + "/tipp/submit").hasRole("TIPPER")
                // user profile update

                .requestMatchers(HttpMethod.GET, BetofficeUrlPath.URL_OFFICE + "/profile/**").hasRole("TIPPER")
                .requestMatchers(HttpMethod.PUT, BetofficeUrlPath.URL_OFFICE + "/profile/**").hasRole("TIPPER")
                .requestMatchers(HttpMethod.POST, BetofficeUrlPath.URL_OFFICE + "/profile/**").hasRole("TIPPER")

                // Community Administration
                .requestMatchers(HttpMethod.GET, BetofficeUrlPath.URL_COMMUNITY_ADMIN + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, BetofficeUrlPath.URL_COMMUNITY_ADMIN + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, BetofficeUrlPath.URL_COMMUNITY_ADMIN + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, BetofficeUrlPath.URL_COMMUNITY_ADMIN + "/**").hasRole("ADMIN")

                // Administration
                .requestMatchers(HttpMethod.GET, BetofficeUrlPath.URL_ADMIM + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, BetofficeUrlPath.URL_ADMIM + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, BetofficeUrlPath.URL_ADMIM + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, BetofficeUrlPath.URL_ADMIM + "/**").hasRole("ADMIN"));

        // Enable OAuth2 login
        http
                .csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).spa())
                .logout(l -> l.logoutUrl("/logout").logoutSuccessUrl("/").permitAll().addLogoutHandler(clearSiteData))
                .oauth2Client(Customizer.withDefaults())
                .oauth2Login(login -> login
                        // TODO Copilot recommandation: Instead of HttpSessionOAuth2AuthorizationRequestRepository, but it does not work with the current frontend implementation. We need to switch to a cookie-based approach to make it work.
                        //.authorizationEndpoint(auth -> auth
                        //        .authorizationRequestRepository(new HttpCookieOAuth2AuthorizationRequestRepository()))
                        // TODO
                        // .authorizationEndpoint(auth -> auth
                        //      .authorizationRequestRepository(new HttpSessionOAuth2AuthorizationRequestRepository()))
                        // .defaultSuccessUrl("http://localhost:9999/login", true)
                        .failureHandler((request, response, exception) -> {
                            // Log the exception so we can see the real cause in the server logs
                            System.out.println("OAuth2 login failed. " + exception);
                            exception.printStackTrace();
                            // Redirect to the application login path with the error flag so the UI can show an error
                            //response.sendRedirect("http://localhost:9999/authentication/login?error");
                        }));

        http.addFilter(new JWTAuthenticationFilter(authenticationManager, authService));
        http.addFilter(new JWTAuthorizationFilter(authenticationManager, authService));
        // http.addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.addAllowedMethod(HttpMethod.PUT);
        configuration.addAllowedMethod(HttpMethod.DELETE);
        source.registerCorsConfiguration("/**", configuration);

        // The old default configuration is missing the PUT method:
        // source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        var x = new LogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(
                    final HttpServletRequest request,
                    final HttpServletResponse response,
                    final Authentication authentication) {

                authService.logout(SecurityConstants.getToken(request));
            }
        };
        return x;
    }

}

class BetofficeUserAccountDetailsService implements UserDetailsService {

    private final UserDao userDao;

    BetofficeUserAccountDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByNickname(Nickname.of(username))
                .orElseThrow(() -> new UsernameNotFoundException("Unknown nickname: " + username));

        return new BetofficeUserDetails(user, user.getRoleTypes());
    }

}

class BetofficeUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final User user;
    private final List<SimpleGrantedAuthority> authorities;

    BetofficeUserDetails(User user, List<RoleType> roleTypes) {
        this.user = user;
        this.authorities = roleTypes.stream().map(RoleType::name).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getNickname().value();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isExcluded();
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return !user.isExcluded();
    }

}