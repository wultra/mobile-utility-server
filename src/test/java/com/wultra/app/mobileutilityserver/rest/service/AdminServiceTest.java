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
package com.wultra.app.mobileutilityserver.rest.service;

import com.wultra.app.mobileutilityserver.rest.model.enums.Platform;
import com.wultra.app.mobileutilityserver.rest.model.request.CreateApplicationVersionRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link AdminService}.
 *
 * @author Lubos Racansky, lubos.racansky@wultra.com
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql
class AdminServiceTest {

    @Autowired
    private AdminService tested;

    @Test
    void testCreateApplicationVersion_successful() {
        final CreateApplicationVersionRequest request = new CreateApplicationVersionRequest();
        request.setPlatform(Platform.ANDROID);
        request.setSuggestedVersion("1.2.3");

        final var result = tested.createApplicationVersion("test-app", request);

        assertNotNull(result.getId());
    }

    @Test
    void testCreateApplicationVersion_alreadyExists() {
        final CreateApplicationVersionRequest request = new CreateApplicationVersionRequest();
        request.setPlatform(Platform.IOS);
        request.setSuggestedVersion("1.2.3");
        request.setMajorOsVersion(11);

        final var result = assertThrows(ConstraintViolationException.class, () -> tested.createApplicationVersion("test-app", request));

        assertEquals("Application version already exists, applicationName=test-app, platform=IOS, majorOsVersion=11", result.getMessage());
    }

    @Test
    void testCreateApplicationVersion_alreadyExists_majorVersionNull() {
        final CreateApplicationVersionRequest request = new CreateApplicationVersionRequest();
        request.setPlatform(Platform.IOS);
        request.setSuggestedVersion("1.2.3");
        request.setMajorOsVersion(null);

        final var result = assertThrows(ConstraintViolationException.class, () -> tested.createApplicationVersion("test-app", request));

        assertEquals("Application version already exists, applicationName=test-app, platform=IOS, majorOsVersion=null", result.getMessage());
    }

}