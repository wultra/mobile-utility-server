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

package com.wultra.app.mobileutilityserver;

import com.google.common.io.BaseEncoding;
import com.wultra.app.mobileutilityserver.rest.http.HttpHeaders;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

/**
 * Simple Gatling load test for the main mobile app endpoint.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
public class CustomerRequestSimulation extends Simulation {

    @Override
    public void before() {
        final ConfigurableApplicationContext app = SpringApplication.run(MobileUtilityServerApplication.class);
        app.registerShutdownHook();
    }

    // Protocol Definition
    final HttpProtocolBuilder httpProtocol = HttpDsl.http
            .baseUrl(System.getProperty("baseUrl", "http://localhost:8080"))
            .acceptHeader("application/json")
            .userAgentHeader("Gatling Performance Test");

    Iterator<Map<String, Object>> feeder =
            Stream.generate((Supplier<Map<String, Object>>) () -> {
                        final String challengeHeader = RandomStringUtils.randomAlphanumeric(16);
                        return Collections.singletonMap("challenge", challengeHeader);
                    }
            ).iterator();

    // Scenario
    final ScenarioBuilder scn = scenario("BasicSimulation")
            .feed(feeder)
            .exec(
                    http("Perf Test")
                            .get("/app/init")
                            .queryParam("appName", System.getProperty("appName", "mobile-app"))
                            .header(HttpHeaders.REQUEST_CHALLENGE, session -> session.getString("challenge"))
            );

    {
        setUp(scn.injectOpen(
                constantUsersPerSec(
                        Integer.getInteger("users", 10)
                ).during(
                        Integer.getInteger("duration", 60)
                )
        )).protocols(httpProtocol);
    }

}
