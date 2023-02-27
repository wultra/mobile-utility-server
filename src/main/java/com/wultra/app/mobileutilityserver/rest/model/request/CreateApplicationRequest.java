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
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Request for creating a new application.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Data
public class CreateApplicationRequest {

    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9-_]{1,254}$", message = "The name must be only in uppercase or lowercase letters, numbers and '-' or '_' characters")
    @Schema(type = "string", example = "mobile-app")
    private String name;

    @NotBlank
    private String displayName;

}
