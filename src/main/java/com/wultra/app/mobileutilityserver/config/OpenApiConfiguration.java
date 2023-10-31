/*
 * Wultra Mobile Utility Server
 * Copyright (C) 2020  Wultra s.r.o.
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

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class used for setting up Swagger documentation.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Mobile Utility Server API",
                version = "1.0",
                license = @License(
                        name = "AGPL-3.0",
                        url = "https://www.gnu.org/licenses/agpl-3.0.en.html"
                ),
                description = "Documentation for the Mobile Utility Server RESTful API published by the Mobile Utility Server.",
                contact = @Contact(
                        name = "Wultra s.r.o.",
                        url = "https://www.wultra.com"
                )
        )
)
public class OpenApiConfiguration {

    @Bean
    public GroupedOpenApi defaultApiGroup() {
        final String[] packages = { "com.wultra.app.mobileutilityserver.rest.controller.api" };
        return GroupedOpenApi.builder()
                .group("mobile-utility-server")
                .packagesToScan(packages)
                .build();
    }

}