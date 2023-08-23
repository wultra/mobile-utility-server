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

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Response with application version.
 *
 * @author Lubos Racansky, lubos.racansky@wultra.com
 */
@Data
public class ApplicationVersionDetailResponse {

    private Platform platform;

    /**
     * Major operation system version, may be {@code null} to match all.
     * <p>
     * For iOS e.g. 12.4.2 it is 12. For Android, it is API level e.g. 29.
     */
    private Integer majorOsVersion;

    private String suggestedVersion;

    private String requiredVersion;

    @NotBlank
    private String messageKey;

    public enum Platform {
        ANDROID,
        IOS
    }

}
