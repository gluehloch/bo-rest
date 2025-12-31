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

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import de.betoffice.service.AuthService;
import de.betoffice.service.SecurityToken;
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

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http, AuthenticationManager authenticationManager)
            throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);

        // .headers().cacheControl().and()

        //                .logout()
        //                .logoutUrl("/logout")
        //                .logoutSuccessHandler(logoutSuccessHandler()).deleteCookies("JSESSIONID")
        //                .and()
        //                .formLogin().and()
        //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        /*
        http.authorizeHttpRequests(authz -> authz
        );
                */

        http.authorizeHttpRequests(authz -> authz /*.anyRequest().permitAll()*/
                //.requestMatchers(antMatcher("/actuator/**")).permitAll()
                //.requestMatchers(antMatcher("/actuator/env/**")).permitAll()
                .requestMatchers(antMatcher(HttpMethod.GET, BetofficeUrlPath.URL_OFFICE + "/**")).permitAll()
                // Authentication
                .requestMatchers(antMatcher(HttpMethod.GET,
                        BetofficeUrlPath.URL_AUTHENTICATION + BetofficeUrlPath.URL_AUTHENTICATION_PING))
                .permitAll()
                .requestMatchers(antMatcher(HttpMethod.POST,
                        BetofficeUrlPath.URL_AUTHENTICATION + BetofficeUrlPath.URL_AUTHENTICATION_LOGIN))
                .permitAll()
                .requestMatchers(antMatcher(HttpMethod.POST,
                        BetofficeUrlPath.URL_AUTHENTICATION + BetofficeUrlPath.URL_AUTHENTICATION_LOGOUT))
                .authenticated()
                // Google OAuth2 endpoints
                .requestMatchers(antMatcher(HttpMethod.GET,
                        "/oauth2/authorization/google")) // TODO Remove Me. Probably not needed...?!?
                .permitAll()
                .requestMatchers(antMatcher(HttpMethod.GET,
                        BetofficeUrlPath.URL_AUTHENTICATION + "/google/callback"))
                .permitAll()
                .requestMatchers(antMatcher(HttpMethod.GET,
                        BetofficeUrlPath.URL_AUTHENTICATION + "/google/status"))
                .authenticated()
                // Send tipp form
                .requestMatchers(antMatcher(HttpMethod.POST, BetofficeUrlPath.URL_OFFICE + "/tipp/submit"))
                .hasRole("TIPPER")
                // user profile update
                .requestMatchers(antMatcher(HttpMethod.GET, BetofficeUrlPath.URL_OFFICE + "/profile/**"))
                .hasRole("TIPPER")
                .requestMatchers(antMatcher(HttpMethod.PUT, BetofficeUrlPath.URL_OFFICE + "/profile/**"))
                .hasRole("TIPPER")
                .requestMatchers(antMatcher(HttpMethod.POST, BetofficeUrlPath.URL_OFFICE + "/profile/**"))
                .hasRole("TIPPER")

                // Community Administration
                .requestMatchers(antMatcher(HttpMethod.GET, BetofficeUrlPath.URL_COMMUNITY_ADMIN + "/**"))
                .hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.PUT, BetofficeUrlPath.URL_COMMUNITY_ADMIN + "/**"))
                .hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.POST, BetofficeUrlPath.URL_COMMUNITY_ADMIN + "/**"))
                .hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.DELETE, BetofficeUrlPath.URL_COMMUNITY_ADMIN + "/**"))
                .hasRole("ADMIN")
                // Administration
                .requestMatchers(antMatcher(HttpMethod.GET, BetofficeUrlPath.URL_ADMIM + "/**")).hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.PUT, BetofficeUrlPath.URL_ADMIM + "/**")).hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.POST, BetofficeUrlPath.URL_ADMIM + "/**")).hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.DELETE, BetofficeUrlPath.URL_ADMIM + "/**")).hasRole("ADMIN"));

        // Enable OAuth2 login
        http.oauth2Login(oauth2 -> oauth2
                // .loginPage("/oauth2/authorization/google")
                .loginPage("http://localhost:9999/betoffice-boot/oauth2/authorization/google")
                .defaultSuccessUrl(BetofficeUrlPath.URL_AUTHENTICATION + "/google/callback", true));
        // Authentication Endpoint
        /*
                http.requestMatchers(HttpMethod.GET,     BetofficeUrlPath.URL_AUTHENTICATION + BetofficeUrlPath.URL_AUTHENTICATION_PING).anonymous()
                .requestMatchers(HttpMethod.POST,    BetofficeUrlPath.URL_AUTHENTICATION + BetofficeUrlPath.URL_AUTHENTICATION_LOGIN).anonymous()
                .requestMatchers(HttpMethod.POST,    BetofficeUrlPath.URL_AUTHENTICATION + BetofficeUrlPath.URL_AUTHENTICATION_LOGOUT).authenticated()
                */

        /*
        .requestMatchers(antMatcher(HttpMethod.POST,   BetofficeUrlPath.URL_OFFICE + "/tipp/submit")).hasRole("TIPPER")
        .requestMatchers(antMatcher(HttpMethod.GET,    BetofficeUrlPath.URL_ADMIM)).hasRole("ADMIN")
        .requestMatchers(antMatcher(HttpMethod.PUT,    BetofficeUrlPath.URL_ADMIM)).hasRole("ADMIN")
        .requestMatchers(antMatcher(HttpMethod.POST,   BetofficeUrlPath.URL_ADMIM)).hasRole("ADMIN")
        .requestMatchers(antMatcher(HttpMethod.DELETE, BetofficeUrlPath.URL_ADMIM)).hasRole("ADMIN")
        */

        //.antMatchers(HttpMethod.GET, "/books/**").hasRole("USER")
        //.antMatchers(HttpMethod.POST, "/books").hasRole("ADMIN")
        //.antMatchers(HttpMethod.PUT, "/books/**").hasRole("ADMIN")
        //.antMatchers(HttpMethod.PATCH, "/books/**").hasRole("ADMIN")
        //.antMatchers(HttpMethod.DELETE, "/books/**").hasRole("ADMIN")

        http.addFilter(new JWTAuthenticationFilter(authenticationManager, authService));
        http.addFilter(new JWTAuthorizationFilter(authenticationManager, authService));

        return http.build();
    }

    //    @Bean
    //    public UserDetailsService userDetailsService() {
    //        return loginService;
    //    }

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