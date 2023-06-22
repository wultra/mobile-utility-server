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

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Request version verification.
 *
 * @author Lubos Racansky, lubos.racansky@wultra.com
 */
@Data
public class VerifyVersionRequest {

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String applicationName;

    @NotBlank
    @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+(-.*)?$", message = "Application version must comply SemVer 2.0")
    @Schema(description = "Application version in SemVer 2.0 format but only MAJOR.MINOR.PATCH are taken into account.",
            pattern = "^\\d+\\.\\d+\\.\\d+(-.*)?$",
            example = "2.5.3",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String applicationVersion;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Platform platform;

    @NotBlank
    @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+(-.*)?|\\d+$", message = "System version must comply SemVer 2.0 or to be a single number.")
    @Schema(
            description = "Operation system version, e.g. '31' for Android or '14.5.1' for iOS.",
            example = "14.5.1",
            pattern = "^\\d+\\.\\d+\\.\\d+(-.*)?|\\d+$",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String systemVersion;

    public enum Platform {
        ANDROID,
        IOS
    }

}
