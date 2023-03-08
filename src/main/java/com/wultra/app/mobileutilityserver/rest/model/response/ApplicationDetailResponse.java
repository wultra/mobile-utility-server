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

import com.wultra.app.mobileutilityserver.rest.model.entity.Domain;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Response with application details.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Data
public class ApplicationDetailResponse {

    private String name;
    private String displayName;
    private String publicKey;

    private final List<Domain> domains = new ArrayList<>();

}
