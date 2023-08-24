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
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Request for creating application version used for forced update functionality.
 *
 * @author Lubos Racansky, lubos.racansky@wultra.com
 */
@Data
public class CreateApplicationVersionRequest {

    private Platform platform;

    @Schema(
            description = """
                    Major operation system version, may be `null` to match all.
                    For iOS e.g. 12.4.2 it is 12. For Android, it is API level e.g. 29.""",
            type = "string",
            example = "12"
    )
    private Integer majorOsVersion;

    @Pattern(regexp = RegexpPatternConstants.SEMVER_2_0, message = "Application version must comply SemVer 2.0")
    @Schema(
            description = "Application version in SemVer 2.0 format but only MAJOR.MINOR.PATCH are taken into account.",
            type = "string",
            pattern = RegexpPatternConstants.SEMVER_2_0,
            example = "3.1.2"
    )
    private String suggestedVersion;

    @Pattern(regexp = RegexpPatternConstants.SEMVER_2_0, message = "Application version must comply SemVer 2.0")
    @Schema(
            description = "Application version in SemVer 2.0 format but only MAJOR.MINOR.PATCH are taken into account.",
            type = "string",
            pattern = RegexpPatternConstants.SEMVER_2_0,
            example = "3.1.2"
    )
    private String requiredVersion;

    @NotBlank
    private String messageKey;

}
