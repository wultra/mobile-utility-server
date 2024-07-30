/*
 * Wultra Mobile Utility Server
 * Copyright (C) 2024  Wultra s.r.o.
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
package com.wultra.app.mobileutilityserver.rest.http;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;

import org.junit.jupiter.api.Test;

/**
 * Test for {@link HttpHeaders}.
 *
 * @author Lubos Racansky, lubos.racansky@wultra.com
 */
class HttpHeadersTest {

    @Test
    void testValidChallengeHeader_success() {
        final String input = Base64.getEncoder().encodeToString(new byte[20]);

        final boolean result = HttpHeaders.validChallengeHeader(input);

        assertTrue(result);
    }

    @Test
    void testValidChallengeHeader_empty() {
        final boolean result = HttpHeaders.validChallengeHeader("");

        assertFalse(result);
    }

    @Test
    void testValidChallengeHeader_tooShort() {
        final String input = Base64.getEncoder().encodeToString(new byte[14]);

        final boolean result = HttpHeaders.validChallengeHeader(input);

        assertFalse(result);
    }

    @Test
    void testValidChallengeHeader_tooLong() {
        final String input = Base64.getEncoder().encodeToString(new byte[33]);

        final boolean result = HttpHeaders.validChallengeHeader(input);

        assertFalse(result);
    }

    @Test
    void testValidChallengeHeader_notBase64() {
        final boolean result = HttpHeaders.validChallengeHeader("&-+");

        assertFalse(result);
    }
}
