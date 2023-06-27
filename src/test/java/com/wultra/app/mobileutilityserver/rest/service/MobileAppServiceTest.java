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

import com.wultra.app.mobileutilityserver.rest.model.request.VerifyVersionRequest;
import com.wultra.app.mobileutilityserver.rest.model.response.VerifyVersionResponse;
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
        final VerifyVersionRequest request = new VerifyVersionRequest();
        request.setApplicationVersion("3.2.1");
        request.setApplicationName("non-existing-app");
        request.setPlatform(VerifyVersionRequest.Platform.IOS);
        request.setSystemVersion("12.4.2");

        final VerifyVersionResponse result = tested.verifyVersion(request);

        assertEquals(VerifyVersionResponse.Update.NOT_REQUIRED, result.getUpdate());
        assertNull(result.getMessage());
    }

    @Test
    void testVerifyVersion_upToDate() {
        final VerifyVersionRequest request = new VerifyVersionRequest();
        request.setApplicationVersion("3.2.1");
        request.setApplicationName("suggested-and-required-app");
        request.setPlatform(VerifyVersionRequest.Platform.IOS);
        request.setSystemVersion("12.4.2");

        final VerifyVersionResponse result = tested.verifyVersion(request);

        assertEquals(VerifyVersionResponse.Update.NOT_REQUIRED, result.getUpdate());
        assertNull(result.getMessage());
    }

    @Test
    void testVerifyVersion_suggestUpdate() {
        final VerifyVersionRequest request = new VerifyVersionRequest();
        request.setApplicationVersion("3.2.1");
        request.setApplicationName("suggested-app");
        request.setPlatform(VerifyVersionRequest.Platform.IOS);
        request.setSystemVersion("12.4.2");

        LocaleContextHolder.setLocale(new Locale("cs"));
        final VerifyVersionResponse result = tested.verifyVersion(request);

        assertEquals(VerifyVersionResponse.Update.SUGGESTED, result.getUpdate());
        assertEquals("Doporučujeme vám aktualizovat aplikaci kvůli vylepšenému výkonu.", result.getMessage());
    }

    @Test
    void testVerifyVersion_requireUpdate() {
        final VerifyVersionRequest request = new VerifyVersionRequest();
        request.setApplicationVersion("3.2.1");
        request.setApplicationName("required-app");
        request.setPlatform(VerifyVersionRequest.Platform.IOS);
        request.setSystemVersion("12.4.2");

        LocaleContextHolder.setLocale(new Locale("cs"));
        final VerifyVersionResponse result = tested.verifyVersion(request);

        assertEquals(VerifyVersionResponse.Update.FORCED, result.getUpdate());
        assertEquals("Upgrade is required to make internet banking working.", result.getMessage(), "Missing text for 'cs', expecting fallback to 'en'");
    }

    @Test
    void testVerifyVersion_whitelistedRequiredApple() {
        final VerifyVersionRequest request = new VerifyVersionRequest();
        request.setApplicationVersion("3.2.1");
        request.setApplicationName("required-app");
        request.setPlatform(VerifyVersionRequest.Platform.IOS);
        request.setSystemVersion("11.2.4");

        final VerifyVersionResponse result = tested.verifyVersion(request);

        assertEquals(VerifyVersionResponse.Update.NOT_REQUIRED, result.getUpdate(), "Newer version is required, but for major version 11 the threshold is lowered.");
        assertNull(result.getMessage());
    }

    @Test
    void testVerifyVersion_whitelistedRequiredAndroid() {
        final VerifyVersionRequest request = new VerifyVersionRequest();
        request.setApplicationVersion("3.2.1");
        request.setApplicationName("required-app");
        request.setPlatform(VerifyVersionRequest.Platform.ANDROID);
        request.setSystemVersion("29");

        final VerifyVersionResponse result = tested.verifyVersion(request);

        assertEquals(VerifyVersionResponse.Update.NOT_REQUIRED, result.getUpdate(), "Newer version is required, but for major version 29 the threshold is lowered.");
        assertNull(result.getMessage());
    }

    @Test
    void testVerifyVersion_whitelistedSuggestedApple() {
        final VerifyVersionRequest request = new VerifyVersionRequest();
        request.setApplicationVersion("3.2.1");
        request.setApplicationName("suggested-app");
        request.setPlatform(VerifyVersionRequest.Platform.IOS);
        request.setSystemVersion("11.2.4");

        final VerifyVersionResponse result = tested.verifyVersion(request);

        assertEquals(VerifyVersionResponse.Update.NOT_REQUIRED, result.getUpdate(), "Newer version is suggested, but for major version 11 the threshold is lowered.");
        assertNull(result.getMessage());
    }

    @Test
    void testVerifyVersion_whitelistedSuggestedAndroid() {
        final VerifyVersionRequest request = new VerifyVersionRequest();
        request.setApplicationVersion("3.2.1");
        request.setApplicationName("suggested-app");
        request.setPlatform(VerifyVersionRequest.Platform.ANDROID);
        request.setSystemVersion("29");

        final VerifyVersionResponse result = tested.verifyVersion(request);

        assertEquals(VerifyVersionResponse.Update.NOT_REQUIRED, result.getUpdate(), "Newer version is suggested, but for major version 29 the threshold is lowered.");
        assertNull(result.getMessage());
    }
}