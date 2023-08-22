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

import com.wultra.app.mobileutilityserver.rest.model.response.VerifyVersionResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test for {@link MobileAppService}.
 *
 * @author Lubos Racansky, lubos.racansky@wultra.com
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql
class MobileAppServiceTest {

    @Autowired
    private MobileAppService tested;

    @Test
    void testVerifyVersion_appDoesNotExist() {
        final VerifyVersionRequest request = VerifyVersionRequest.builder()
                .applicationVersion("3.2.1")
                .applicationName("non-existing-app")
                .platform(VerifyVersionRequest.Platform.IOS)
                .systemVersion("12.4.2")
                .build();

        final VerifyVersionResult result = tested.verifyVersion(request);

        assertEquals(VerifyVersionResult.Update.NOT_REQUIRED, result.getUpdate());
        assertNull(result.getMessage());
    }

    @Test
    void testVerifyVersion_upToDate() {
        final VerifyVersionRequest request = VerifyVersionRequest.builder()
                .applicationVersion("3.2.1")
                .applicationName("suggested-and-required-app")
                .platform(VerifyVersionRequest.Platform.IOS)
                .systemVersion("12.4.2")
                .build();

        final VerifyVersionResult result = tested.verifyVersion(request);

        assertEquals(VerifyVersionResult.Update.NOT_REQUIRED, result.getUpdate());
        assertNull(result.getMessage());
    }

    @Test
    void testVerifyVersion_suggestUpdate() {
        final VerifyVersionRequest request = VerifyVersionRequest.builder()
                .applicationVersion("3.2.1")
                .applicationName("suggested-app")
                .platform(VerifyVersionRequest.Platform.IOS)
                .systemVersion("12.4.2")
                .build();

        LocaleContextHolder.setLocale(new Locale("cs"));
        final VerifyVersionResult result = tested.verifyVersion(request);

        assertEquals(VerifyVersionResult.Update.SUGGESTED, result.getUpdate());
        assertEquals("Doporučujeme vám aktualizovat aplikaci kvůli vylepšenému výkonu.", result.getMessage());
    }

    @Test
    void testVerifyVersion_requireUpdate() {
        final VerifyVersionRequest request = VerifyVersionRequest.builder()
                .applicationVersion("3.2.1")
                .applicationName("required-app")
                .platform(VerifyVersionRequest.Platform.IOS)
                .systemVersion("12.4.2")
                .build();

        LocaleContextHolder.setLocale(new Locale("cs"));
        final VerifyVersionResult result = tested.verifyVersion(request);

        assertEquals(VerifyVersionResult.Update.FORCED, result.getUpdate());
        assertEquals("Upgrade is required to make internet banking working.", result.getMessage(), "Missing text for 'cs', expecting fallback to 'en'");
    }

    @Test
    void testVerifyVersion_whitelistedRequiredApple() {
        final VerifyVersionRequest request = VerifyVersionRequest.builder()
                .applicationVersion("3.2.1")
                .applicationName("required-app")
                .platform(VerifyVersionRequest.Platform.IOS)
                .systemVersion("11.2.4")
                .build();

        final VerifyVersionResult result = tested.verifyVersion(request);

        assertEquals(VerifyVersionResult.Update.NOT_REQUIRED, result.getUpdate(), "Newer version is required, but for major version 11 the threshold is lowered.");
        assertNull(result.getMessage());
    }

    @Test
    void testVerifyVersion_whitelistedRequiredAndroid() {
        final VerifyVersionRequest request = VerifyVersionRequest.builder()
                .applicationVersion("3.2.1")
                .applicationName("required-app")
                .platform(VerifyVersionRequest.Platform.ANDROID)
                .systemVersion("29")
                .build();

        final VerifyVersionResult result = tested.verifyVersion(request);

        assertEquals(VerifyVersionResult.Update.NOT_REQUIRED, result.getUpdate(), "Newer version is required, but for major version 29 the threshold is lowered.");
        assertNull(result.getMessage());
    }

    @Test
    void testVerifyVersion_whitelistedSuggestedApple() {
        final VerifyVersionRequest request = VerifyVersionRequest.builder()
                .applicationVersion("3.2.1")
                .applicationName("suggested-app")
                .platform(VerifyVersionRequest.Platform.IOS)
                .systemVersion("11.2.4")
                .build();

        final VerifyVersionResult result = tested.verifyVersion(request);

        assertEquals(VerifyVersionResult.Update.NOT_REQUIRED, result.getUpdate(), "Newer version is suggested, but for major version 11 the threshold is lowered.");
        assertNull(result.getMessage());
    }

    @Test
    void testVerifyVersion_whitelistedSuggestedAndroid() {
        final VerifyVersionRequest request = VerifyVersionRequest.builder()
                .applicationVersion("3.2.1")
                .applicationName("suggested-app")
                .platform(VerifyVersionRequest.Platform.ANDROID)
                .systemVersion("29")
                .build();

        final VerifyVersionResult result = tested.verifyVersion(request);

        assertEquals(VerifyVersionResult.Update.NOT_REQUIRED, result.getUpdate(), "Newer version is suggested, but for major version 29 the threshold is lowered.");
        assertNull(result.getMessage());
    }
}