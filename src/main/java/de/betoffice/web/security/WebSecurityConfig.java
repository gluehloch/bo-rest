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

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
import de.winkler.betoffice.storage.User;

/**
 * Security configuration.
 *
 * @author by Andre Winkler
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthService authService;
    
    @Autowired
    CustomAuthenticationProvider customAuthenticationProvider;
    
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
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.authenticationProvider(customAuthenticationProvider);
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
                .cors().disable()
                .csrf().disable()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutSuccessHandler()).deleteCookies("JSESSIONID")
                .and()
                //                .formLogin().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), authService))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), authService))
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/demo/ping").permitAll()

                .antMatchers(HttpMethod.GET, "/order").hasAnyRole("USER")
                .antMatchers(HttpMethod.POST, "/order").hasAnyRole("USER")
                .antMatchers(HttpMethod.DELETE, "/order").hasAnyRole("USER")
                .antMatchers(HttpMethod.PUT, "/order").hasAnyRole("USER")

                .antMatchers(HttpMethod.GET, "/user").permitAll()
                .antMatchers(HttpMethod.PUT, "/user").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/user").hasAnyRole("USER", "ADMIN")

                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .antMatchers(HttpMethod.POST, "/logout").hasRole("USER"); // TODO
        //.antMatchers(HttpMethod.GET, "/books/**").hasRole("USER")
        //.antMatchers(HttpMethod.POST, "/books").hasRole("ADMIN")
        //.antMatchers(HttpMethod.PUT, "/books/**").hasRole("ADMIN")
        //.antMatchers(HttpMethod.PATCH, "/books/**").hasRole("ADMIN")
        //.antMatchers(HttpMethod.DELETE, "/books/**").hasRole("ADMIN")

        //                .and()
        //                .csrf().disable()
        //                .formLogin().disable();
    }
    
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return loginService;
//    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        var x = new LogoutSuccessHandler() {

            @Override
            public void onLogoutSuccess(
            		HttpServletRequest request,
            		HttpServletResponse response,
            		Authentication authentication) 
            				throws IOException, ServletException {
            	
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
        User user = userDao.findByNickname(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unknown nickname: " + username));

        return new BetofficeUserDetails(user);
    }

}

class BetofficeUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;

	private final User user;
    
    BetofficeUserDetails(User user) {
        this.user = user;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getNickname();
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
