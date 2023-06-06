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
package com.wultra.app.mobileutilityserver.rest.model.request;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for {@link VerifyVersionRequest}.
 *
 * @author Lubos Racansky, lubos.racansky@wultra.com
 */
class VerifyVersionRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void testValidApple() {
        final VerifyVersionRequest tested = new VerifyVersionRequest();
        tested.setApplicationName("test-app");
        tested.setApplicationVersion("3.20.1");
        tested.setPlatform(VerifyVersionRequest.Platform.APPLE);
        tested.setSystemVersion("12.4.1");

        final Set<ConstraintViolation<VerifyVersionRequest>> result = validator.validate(tested);
        assertEquals(0, result.size());
    }

    @Test
    void testValidAndroid() {
        final VerifyVersionRequest tested = new VerifyVersionRequest();
        tested.setApplicationName("test-app");
        tested.setApplicationVersion("3.2.1");
        tested.setPlatform(VerifyVersionRequest.Platform.APPLE);
        tested.setSystemVersion("29");

        final Set<ConstraintViolation<VerifyVersionRequest>> result = validator.validate(tested);
        assertEquals(0, result.size());
    }

    @Test
    void testInvalid() {
        final VerifyVersionRequest tested = new VerifyVersionRequest();
        tested.setApplicationVersion("3.2");
        tested.setSystemVersion("1.2");

        final Set<ConstraintViolation<VerifyVersionRequest>> result = validator.validate(tested);
        assertEquals(4, result.size());

        final Set<String> paths = result.stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Path::toString)
                .collect(Collectors.toSet());
        assertTrue(paths.contains("applicationName"));
        assertTrue(paths.contains("applicationVersion"));
        assertTrue(paths.contains("platform"));
        assertTrue(paths.contains("systemVersion"));
    }

}