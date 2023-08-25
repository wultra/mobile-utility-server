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
import com.wultra.app.mobileutilityserver.rest.model.request.RegexpPatternConstants;
import com.wultra.app.mobileutilityserver.rest.model.response.AppInitResponse;
import com.wultra.app.mobileutilityserver.rest.model.response.PublicKeyResponse;
import com.wultra.app.mobileutilityserver.rest.model.response.VerifyVersionResult;
import com.wultra.app.mobileutilityserver.rest.service.CertificateFingerprintService;
import com.wultra.app.mobileutilityserver.rest.service.MobileAppService;
import com.wultra.app.mobileutilityserver.rest.service.VerifyVersionRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
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
@Validated
@Slf4j
public class AppInitializationController {

    private final CertificateFingerprintService certificateFingerprintService;
    private final MobileAppService mobileAppService;

    @Autowired
    public AppInitializationController(CertificateFingerprintService certificateFingerprintService, MobileAppService mobileAppService) {
        this.certificateFingerprintService = certificateFingerprintService;
        this.mobileAppService = mobileAppService;
    }

    @GetMapping
    @Parameter(
            name = HttpHeaders.REQUEST_CHALLENGE,
            description = "Challenge that is included in the response signature. Must be at least 16 bytes long.",
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string")
    )
    @Parameter(
            name = QueryParams.QUERY_PARAM_APP_VERSION,
            description = "Application version in SemVer 2.0 format but only MAJOR.MINOR.PATCH are taken into account.",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string", pattern = RegexpPatternConstants.SEMVER_2_0),
            example = "3.1.2"
    )
    @Parameter(
            name = QueryParams.QUERY_PARAM_OS_VERSION,
            description = "Operation system version, e.g. `31` for Android or `14.5.1` for iOS.",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string", pattern = "^\\d+\\.\\d+\\.\\d+(-.*)?|\\d+$"),
            example = "14.5.1"
    )
    public AppInitResponse appInit(
            @RequestParam(QueryParams.QUERY_PARAM_APP_NAME) String applicationName,
            @Pattern(regexp = RegexpPatternConstants.SEMVER_2_0, message = "Application version must comply SemVer 2.0")
            @RequestParam(value = QueryParams.QUERY_PARAM_APP_VERSION, required = false) String applicationVersion,
            @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+(-.*)?|\\d+$", message = "System version must comply SemVer 2.0 or to be a single number.")
            @RequestParam(value = QueryParams.QUERY_PARAM_OS_VERSION, required = false) String systemVersion,
            @RequestParam(value = QueryParams.QUERY_PARAM_PLATFORM, required = false) Platform platform,
            @RequestHeader(value = HttpHeaders.REQUEST_CHALLENGE, required = false) String challengeHeader
    ) throws InvalidChallengeHeaderException, AppNotFoundException {

        // Validate HTTP headers
        if (!HttpHeaders.validChallengeHeader(challengeHeader)) {
            throw new InvalidChallengeHeaderException();
        }

        // Check if an app exists
        final boolean appExists = mobileAppService.appExists(applicationName);
        if (!appExists) {
            throw new AppNotFoundException(applicationName);
        }

        // Find the fingerprints
        final List<CertificateFingerprint> fingerprints = certificateFingerprintService.findCertificateFingerprintsByAppName(applicationName);

        if (StringUtils.hasText(applicationVersion) && StringUtils.hasText(systemVersion) && platform != null) {
            final VerifyVersionRequest verifyVersionRequest = VerifyVersionRequest.builder()
                    .applicationName(applicationName)
                    .applicationVersion(applicationVersion)
                    .systemVersion(systemVersion)
                    .platform(convert(platform))
                    .build();
            final VerifyVersionResult verifyVersionResult = mobileAppService.verifyVersion(verifyVersionRequest);

            return new AppInitResponse(fingerprints, verifyVersionResult);
        } else {
            logger.debug("Context for verifying version not provided for application name: {}", applicationName);
            return new AppInitResponse(fingerprints, null);
        }
    }

    @GetMapping("public-key")
    public PublicKeyResponse publicKeys(@RequestParam(QueryParams.QUERY_PARAM_APP_NAME) String appName) throws PublicKeyNotFoundException {
        final String publicKey = mobileAppService.publicKey(appName);
        if (publicKey == null) {
            throw new PublicKeyNotFoundException(appName);
        }
        return new PublicKeyResponse(publicKey);
    }

    private static VerifyVersionRequest.Platform convert(final Platform source) {
        return switch (source) {
            case ANDROID -> VerifyVersionRequest.Platform.ANDROID;
            case IOS -> VerifyVersionRequest.Platform.IOS;
        };
    }

    public enum Platform {
        ANDROID,
        IOS
    }

}
