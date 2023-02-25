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

package com.wultra.app.mobileutilityserver.rest.model.converter;

import com.wultra.app.mobileutilityserver.database.model.MobileApp;
import com.wultra.app.mobileutilityserver.rest.model.response.ApplicationDetailResponse;
import org.springframework.stereotype.Component;

/**
 * Converter class for mobile app and domain info.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Component
public class MobileAppConverter {

    /**
     * Convert mobile app model to application detail response.
     * @param source Mobile app DB model.
     * @return Mobile app REST response.
     */
    public ApplicationDetailResponse convertMobileApp(MobileApp source) {
        if (source == null) {
            return null;
        }
        final ApplicationDetailResponse destination = new ApplicationDetailResponse();
        destination.setName(source.getName());
        destination.setDisplayName(source.getDisplayName());
        destination.setPublicKey(source.getSigningPublicKey());
        return destination;
    }
}
