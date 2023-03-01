/*
 * Wultra Mobile Utility Server
 * Copyright (C) 2023  Wultra s.r.o.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wultra.app.mobileutilityserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Security configuration.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    @Value("${mobile-utility-server.security.auth.basicHttp.stateless}")
    private boolean stateless;

    @Value("${mobile-utility-server.security.auth.algorithm}")
    private String algorithm;

    @Value("${mobile-utility-server.security.auth.bcrypt.cycles}")
    private int bcryptCycles;

    private final String[] supportedHashAlgorithms = { "SHA-256", "bcrypt" };

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        final InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("admin")
                .password(passwordEncoder.encode("admin"))
                .roles("ADMIN")
                .build());
        return manager;
    }

    /**
     * Configure SHA-256 or bcrypt password encoder. Note that since the passwords are technical, using old SHA-256
     * algorithm does not cause security issues. Bcrypt is used as default in case no prefix is specified.
     * See the following URL for constant details:
     * <a href="https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/crypto/factory/PasswordEncoderFactories.html#createDelegatingPasswordEncoder()">PasswordEncoderFactories.createDelegatingPasswordEncoder()</a>
     * @return Delegating password encoder.
     */
    @Bean
    @SuppressWarnings({"deprecation", "java:S5344"})
    public PasswordEncoder passwordEncoder() throws NoSuchAlgorithmException {
        if (!List.of(supportedHashAlgorithms).contains(algorithm)) {
            throw new NoSuchAlgorithmException(String.format("Unsupported algorithm specified: %s, must be one of: %s.", algorithm, Arrays.toString(supportedHashAlgorithms)));
        }
        final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(bcryptCycles);
        final MessageDigestPasswordEncoder sha256 = new MessageDigestPasswordEncoder("SHA-256");
        final Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", bcrypt);
        encoders.put("SHA-256", sha256);
        final DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(algorithm, encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(bcrypt); // try using bcrypt as default, for backward compatibility
        return passwordEncoder;
    }

    /**
     * Configure security filter chain.
     * @param http HTTP configuration.
     * @return Security filter chain.
     * @throws Exception In case a configuration error occurs.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            .and()
                .httpBasic()
            .and()
                .sessionManagement()
                .sessionCreationPolicy(stateless ? SessionCreationPolicy.STATELESS : SessionCreationPolicy.IF_REQUIRED);

        return http.build();
    }

}
