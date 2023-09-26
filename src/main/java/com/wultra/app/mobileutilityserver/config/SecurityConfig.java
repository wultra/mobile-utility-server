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
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Security configuration.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${mobile-utility-server.security.auth.basicHttp.stateless}")
    private boolean stateless;

    @Value("${mobile-utility-server.security.auth.algorithm}")
    private String algorithm;

    @Value("${mobile-utility-server.security.auth.bcrypt.cycles}")
    private int bcryptCycles;

    private final String[] supportedHashAlgorithms = { "SHA-256", "bcrypt" };

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        final JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery(
                "SELECT username, password, enabled FROM mus_user where username = ?"
        );
        manager.setAuthoritiesByUsernameQuery(
                "SELECT u.username, a.authority FROM mus_user_authority a, mus_user u "
                        + " WHERE u.username = ? AND u.id = a.user_id"
        );
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
        final Map<String, PasswordEncoder> encoders = Map.of(
                "bcrypt", bcrypt,
                "SHA-256", sha256
        );
        final DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(algorithm, encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(bcrypt); // try using bcrypt as default
        return passwordEncoder;
    }

    /**
     * Configure security filter chain.
     *
     * @param http HTTP configuration.
     * @return Security filter chain.
     * @throws Exception In case a configuration error occurs.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(customizer ->
                        customizer.sessionCreationPolicy(stateless ? SessionCreationPolicy.STATELESS : SessionCreationPolicy.IF_REQUIRED))
                .build();
    }

}
