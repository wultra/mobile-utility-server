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

import com.wultra.app.mobileutilityserver.rest.model.enums.Platform;
import javax.validation.*;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link CreateApplicationVersionRequest}.
 *
 * @author Lubos Racansky, lubos.racansky@wultra.com
 */
class CreateApplicationVersionRequestTest {

    private final Validator validator = createValidator();

    @Test
    void testValid() {
        final var tested = new CreateApplicationVersionRequest();
        tested.setPlatform(Platform.ANDROID);
        tested.setMajorOsVersion(11);
        tested.setRequiredVersion("1.2.3");
        tested.setSuggestedVersion("2.4.6");
        tested.setMessageKey("required-app.internet-banking");

        final var result = validator.validate(tested);

        assertTrue(result.isEmpty());
    }

    @Test
    void testInvalid() {
        final var tested = new CreateApplicationVersionRequest();
        tested.setRequiredVersion("1.2");
        tested.setSuggestedVersion("2.4");

        final var result = validator.validate(tested);

        assertAll(
                () -> assertEquals(3, result.size()),
                () -> {
                    final var actualPaths = result.stream()
                            .map(ConstraintViolation::getPropertyPath)
                            .map(Path::toString)
                            .collect(Collectors.toSet());

                    final var expectedPaths = Set.of("requiredVersion", "suggestedVersion", "platform");
                    assertEquals(expectedPaths, actualPaths);
                });
    }

    private static Validator createValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator();
        }
    }

}
