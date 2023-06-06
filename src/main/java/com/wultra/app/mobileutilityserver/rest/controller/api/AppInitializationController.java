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
package com.wultra.app.mobileutilityserver.rest.controller.api;

import com.wultra.app.mobileutilityserver.rest.errorhandling.AppNotFoundException;
import com.wultra.app.mobileutilityserver.rest.errorhandling.InvalidChallengeHeaderException;
import com.wultra.app.mobileutilityserver.rest.errorhandling.PublicKeyNotFoundException;
import com.wultra.app.mobileutilityserver.rest.http.HttpHeaders;
import com.wultra.app.mobileutilityserver.rest.http.QueryParams;
import com.wultra.app.mobileutilityserver.rest.model.entity.CertificateFingerprint;
import com.wultra.app.mobileutilityserver.rest.model.request.VerifyVersionRequest;
import com.wultra.app.mobileutilityserver.rest.model.response.AppInitResponse;
import com.wultra.app.mobileutilityserver.rest.model.response.PublicKeyResponse;
import com.wultra.app.mobileutilityserver.rest.model.response.VerifyVersionResponse;
import com.wultra.app.mobileutilityserver.rest.service.CertificateFingerprintService;
import com.wultra.app.mobileutilityserver.rest.service.MobileAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    private final CertificateFingerprintService certificateFingerprintService;
    private final MobileAppService mobileAppService;

    @Autowired
    public AppInitializationController(CertificateFingerprintService certificateFingerprintService, MobileAppService mobileAppService) {
        this.certificateFingerprintService = certificateFingerprintService;
        this.mobileAppService = mobileAppService;
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
    public AppInitResponse appInit(
            @RequestParam(QueryParams.QUERY_PARAM_APP_NAME) String appName,
            @RequestHeader(value = HttpHeaders.REQUEST_CHALLENGE, required = false) String challengeHeader
    ) throws InvalidChallengeHeaderException, AppNotFoundException {

        // Validate HTTP headers
        if (!HttpHeaders.validChallengeHeader(challengeHeader)) {
            throw new InvalidChallengeHeaderException();
        }

        // Check if an app exists
        final boolean appExists = mobileAppService.appExists(appName);
        if (!appExists) {
            throw new AppNotFoundException(appName);
        }

        // Find the fingerprints
        final List<CertificateFingerprint> fingerprints = certificateFingerprintService.findCertificateFingerprintsByAppName(appName);

        // Return the response
        return new AppInitResponse(fingerprints);
    }

    @GetMapping("public-key")
    public PublicKeyResponse publicKeys(@RequestParam(QueryParams.QUERY_PARAM_APP_NAME) String appName) throws PublicKeyNotFoundException {
        final String publicKey = mobileAppService.publicKey(appName);
        if (publicKey == null) {
            throw new PublicKeyNotFoundException(appName);
        }
        return new PublicKeyResponse(publicKey);
    }

    @Operation(
            summary = "Verify application version.",
            description = "Verify whether application version is up to date or may be updated or should be updated."
    )
    @PostMapping("verify-version")
    public VerifyVersionResponse verifyVersion(@Valid @RequestBody final VerifyVersionRequest request) {
        return mobileAppService.verifyVersion(request);
    }

}
