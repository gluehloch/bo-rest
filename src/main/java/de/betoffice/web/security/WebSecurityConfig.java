/*
 * ============================================================================
 * Project betoffice-jweb
 * Copyright (c) 2000-2021 by Andre Winkler. All rights reserved.
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
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import de.winkler.betoffice.dao.UserDao;
import de.winkler.betoffice.service.AuthService;
import de.winkler.betoffice.service.SecurityToken;
import de.winkler.betoffice.storage.Nickname;
import de.winkler.betoffice.storage.User;
import de.winkler.betoffice.storage.enums.RoleType;

/**
 * Security configuration.
 *
 * @author by Andre Winkler
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String BO_OFFICE = "/bo/office";
    private static final String BO_ADMIM = "/bo/chiefoperator";
    
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
    
    /**
     * Funktioniert dieses Konstrukt aus @Bean und @Autowired?
     *
     * @param userDetailsService ...
     * @return ...
     */
    @Bean
    public DaoAuthenticationProvider authProvider(@Autowired UserDetailsService userDetailsService, @Autowired PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
    
    @Override
    public void configure(AuthenticationManagerBuilder builder) {
        builder.authenticationProvider(authenticationProvider);
    }
    
    /* Beispiel: Manuelles Setzen des Authentication-Tokens. Wenn ich das Login selber implementiere? Oder wann? */
//    private void xxxx() {
//    	UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(user, pass);
//    	Authentication auth = authenticationManager().authenticate(authReq);
//      	SecurityContext sc = SecurityContextHolder.getContext();
//    	sc.setAuthentication(auth);    	
//    }

    // Secure the endpoins with HTTP Basic authentication
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // .headers().cacheControl().and()
                .cors().and()
                .csrf().disable()
//                .logout()
//                .logoutUrl("/logout")
//                .logoutSuccessHandler(logoutSuccessHandler()).deleteCookies("JSESSIONID")
//                .and()
                //                .formLogin().and()
                //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), authService))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), authService))
                .authorizeRequests()
                //.antMatchers(HttpMethod.GET, "/**").permitAll()
                //.antMatchers(HttpMethod.GET, "/demo/ping").permitAll()
                //.antMatchers(HttpMethod.GET, "/bo/office/**").denyAll()
//                .antMatchers(HttpMethod.GET, "/order").hasAnyRole("USER")
//                .antMatchers(HttpMethod.POST, "/order").hasAnyRole("USER")
//                .antMatchers(HttpMethod.DELETE, "/order").hasAnyRole("USER")
//                .antMatchers(HttpMethod.PUT, "/order").hasAnyRole("USER")
//
//                .antMatchers(HttpMethod.GET, "/user").permitAll()
//                .antMatchers(HttpMethod.PUT, "/user").hasAnyRole("USER", "ADMIN")
//                .antMatchers(HttpMethod.POST, "/user").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.GET,    BO_OFFICE + "/ping").permitAll()
                .antMatchers(HttpMethod.POST,   BO_OFFICE + "/login").permitAll()
                .antMatchers(HttpMethod.POST,   BO_OFFICE + "/logout").hasRole("TIPPER")
                .antMatchers(HttpMethod.POST,   BO_OFFICE + "/tipp/submit").hasRole("TIPPER")
                .antMatchers(HttpMethod.GET,    BO_ADMIM).hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT,    BO_ADMIM).hasRole("ADMIN")
                .antMatchers(HttpMethod.POST,   BO_ADMIM).hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, BO_ADMIM).hasRole("ADMIN")
        ;
        //.antMatchers(HttpMethod.GET, "/books/**").hasRole("USER")
        //.antMatchers(HttpMethod.POST, "/books").hasRole("ADMIN")
        //.antMatchers(HttpMethod.PUT, "/books/**").hasRole("ADMIN")
        //.antMatchers(HttpMethod.PATCH, "/books/**").hasRole("ADMIN")
        //.antMatchers(HttpMethod.DELETE, "/books/**").hasRole("ADMIN")
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
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                /* throws IOException, ServletException */
            	SecurityToken securityToken = null; // TODO Wo bekommen ich den denn her? Aus dem Request vermutlich!!!
            	authService.logout(null);
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
        this.authorities = roleTypes.stream().map(RoleType::name).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
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
