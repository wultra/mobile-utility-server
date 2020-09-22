/*
 * Wultra Mobile Utility Server
 * Copyright (C) 2020  Wultra s.r.o.
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
package com.wultra.app.mobileutilityserver.rest.controller;

import com.wultra.app.mobileutilityserver.rest.errorhandling.PublicKeyNotFoundException;
import com.wultra.app.mobileutilityserver.rest.http.HttpHeaders;
import com.wultra.app.mobileutilityserver.rest.http.QueryParams;
import com.wultra.app.mobileutilityserver.rest.model.response.PublicKeyResponse;
import com.wultra.app.mobileutilityserver.rest.service.MobileAppDAO;
import com.wultra.app.mobileutilityserver.rest.service.SslPinningDAO;
import com.wultra.app.mobileutilityserver.rest.model.response.AppInitResponse;
import com.wultra.app.mobileutilityserver.rest.model.entity.SslPinningFingerprint;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller with generic information needed for app initialization.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@RestController
@RequestMapping("app/init")
@Tag(name = "App Initialization Controller")
public class AppInitializationController {

    private final SslPinningDAO sslPinningDAO;
    private final MobileAppDAO mobileAppDAO;

    @Autowired
    public AppInitializationController(SslPinningDAO sslPinningDAO, MobileAppDAO mobileAppDAO) {
        this.sslPinningDAO = sslPinningDAO;
        this.mobileAppDAO = mobileAppDAO;
    }

    @GetMapping
    @Parameters({
            @Parameter(
                    name = HttpHeaders.REQUEST_CHALLENGE,
                    description = "Challenge that is included in the response signature. Must be at least 16 bytes long.",
                    in = ParameterIn.HEADER,
                    schema = @Schema(type = "string")
            )
    })
    public AppInitResponse appInit(@RequestParam(QueryParams.QUERY_PARAM_APP_NAME) String appName) {
        final AppInitResponse response = new AppInitResponse();

        final List<SslPinningFingerprint> fingerprints = sslPinningDAO.findSslPinningFingerprintsByAppName(appName);
        response.getFingerprints().addAll(fingerprints);

        return response;
    }

    @GetMapping("public-key")
    public PublicKeyResponse publicKeys(@RequestParam(QueryParams.QUERY_PARAM_APP_NAME) String appName) throws PublicKeyNotFoundException {
        final String publicKey = mobileAppDAO.publicKey(appName);
        if (publicKey == null) {
            throw new PublicKeyNotFoundException(appName);
        }
        return new PublicKeyResponse(publicKey);
    }

}
