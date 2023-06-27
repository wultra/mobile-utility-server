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

package com.wultra.app.mobileutilityserver.rest.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Response version verification.
 *
 * @author Lubos Racansky, lubos.racansky@wultra.com
 */
@Getter
@EqualsAndHashCode
@ToString
@Builder
public class VerifyVersionResponse {

    @lombok.NonNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Update update;

    @Schema(
            description = "Optional localized message, should be filled when the status is 'SUGGEST_UPDATE' or 'REQUIRE_UPDATE'.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String message;

    /**
     * Create ok response.
     *
     * @return ok response
     */
    public static VerifyVersionResponse ok() {
        return VerifyVersionResponse.builder()
                .update(Update.NOT_REQUIRED)
                .build();
    }

    public enum Update {
        NOT_REQUIRED,
        SUGGESTED,
        FORCED
    }

}
