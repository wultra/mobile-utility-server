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
package com.wultra.app.mobileutilityserver.rest.service;

import com.wultra.app.mobileutilityserver.database.model.LocalizedTextEntity;
import com.wultra.app.mobileutilityserver.database.model.MobileAppEntity;
import com.wultra.app.mobileutilityserver.database.model.MobileAppVersionEntity;
import com.wultra.app.mobileutilityserver.database.repo.LocalizedTextRepository;
import com.wultra.app.mobileutilityserver.database.repo.MobileAppRepository;
import com.wultra.app.mobileutilityserver.database.repo.MobileAppVersionRepository;
import com.wultra.app.mobileutilityserver.rest.model.response.VerifyVersionResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

/**
 * Service acting as a DAO for mobile application related objects.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class MobileAppService {

    private final MobileAppRepository mobileAppRepository;

    private final MobileAppVersionRepository mobileAppVersionRepository;

    private final LocalizedTextRepository localizedTextRepository;

    private final boolean versionVerificationEnabled;

    @Autowired
    public MobileAppService(
            final MobileAppRepository mobileAppRepository,
            final MobileAppVersionRepository mobileAppVersionRepository,
            final LocalizedTextRepository localizedTextRepository,
            @Value("${mobile-utility-server.features.version-verification.enabled}") final boolean versionVerificationEnabled) {

        this.mobileAppRepository = mobileAppRepository;
        this.mobileAppVersionRepository = mobileAppVersionRepository;
        this.localizedTextRepository = localizedTextRepository;
        this.versionVerificationEnabled = versionVerificationEnabled;
    }

    /**
     * Checks if an app with a provided name exists.
     * @param appName App name.
     * @return True in case the app with given name exists, false otherwise.
     */
    public boolean appExists(String appName) {
        return mobileAppRepository.existsByName(appName);
    }

    /**
     * Return a private key (Base64 encoded value) of an app with provided app name.
     *
     * @param appName App name.
     * @return Private key encoded as Base64 representation of the embedded big integer, or null
     * if app with provided name does not exist.
     */
    public String privateKey(String appName) {
        final MobileAppEntity mobileAppEntity = mobileAppRepository.findFirstByName(appName);
        if (mobileAppEntity == null) {
            return null;
        }
        return mobileAppEntity.getSigningPrivateKey();
    }

    /**
     * Return a private key (Base64 encoded value) of an app with provided app name.
     *
     * @param appName App name.
     * @return Private key encoded as Base64 representation of the embedded big integer, or null
     * if app with provided name does not exist.
     */
    public String publicKey(String appName) {
        final MobileAppEntity mobileAppEntity = mobileAppRepository.findFirstByName(appName);
        if (mobileAppEntity == null) {
            return null;
        }
        return mobileAppEntity.getSigningPublicKey();
    }


    /**
     * Verify application version.
     *
     * @param request verify request
     * @return verify response or {@code null} if feature disabled
     */
    public VerifyVersionResult verifyVersion(final VerifyVersionRequest request) {

        if (!versionVerificationEnabled) {
            logger.debug("Skipping application version verification, feature disabled");
            return null;
        }

        final String applicationName = request.getApplicationName();
        final MobileAppVersionEntity.Platform platform = convert(request.getPlatform());
        final int majorSystemVersion = new DefaultArtifactVersion(request.getSystemVersion()).getMajorVersion();
        final Optional<MobileAppVersionEntity> applicationVersion = findApplicationVersion(applicationName, platform, majorSystemVersion);
        if (applicationVersion.isEmpty()) {
            logger.info("Application name: {}, platform: {} is not configured, returning OK", applicationName, platform);
            return VerifyVersionResult.ok();
        }

        return verifyVersion(applicationVersion.get(), request);
    }

    private static MobileAppVersionEntity.Platform convert(final VerifyVersionRequest.Platform platform) {
        return switch(platform) {
            case ANDROID -> MobileAppVersionEntity.Platform.ANDROID;
            case IOS -> MobileAppVersionEntity.Platform.IOS;
        };
    }

    private Optional<MobileAppVersionEntity> findApplicationVersion(final String applicationName, final MobileAppVersionEntity.Platform platform, final int majorSystemVersion) {
        final Optional<MobileAppVersionEntity> applicationVersion = mobileAppVersionRepository.findFirstByApplicationNameAndPlatformAndMajorOsVersion(applicationName, platform, majorSystemVersion);
        if (applicationVersion.isPresent()) {
            logger.debug("Found exact match for applicationName: {}, platform: {} and majorSystemVersion: {}", applicationName, platform, majorSystemVersion);
            return applicationVersion;
        }
        logger.debug("Looking for applicationName: {} and platform: {} without specific majorSystemVersion.", applicationName, platform);
        return mobileAppVersionRepository.findFirstByApplicationNameAndPlatform(applicationName, platform);
    }

    private VerifyVersionResult verifyVersion(final MobileAppVersionEntity applicationVersion, final VerifyVersionRequest request) {
        logger.debug("Verifying {}, {} ", applicationVersion, request);
        final DefaultArtifactVersion requiredVersion = parseVersion(applicationVersion.getRequiredVersion());
        final DefaultArtifactVersion suggestedVersion = parseVersion(applicationVersion.getSuggestedVersion());
        final DefaultArtifactVersion currentVersion = parseVersion(request.getApplicationVersion());

        if (requiredVersion != null && requiredVersion.compareTo(currentVersion) > 0) {
            return VerifyVersionResult.builder()
                    .update(VerifyVersionResult.Update.FORCED)
                    .message(fetchMessage(applicationVersion.getMessageKey()))
                    .build();
        }

        if (suggestedVersion != null && suggestedVersion.compareTo(currentVersion) > 0) {
            return VerifyVersionResult.builder()
                    .update(VerifyVersionResult.Update.SUGGESTED)
                    .message(fetchMessage(applicationVersion.getMessageKey()))
                    .build();
        }

        return VerifyVersionResult.ok();
    }

    private String fetchMessage(final String key) {
        if (key == null) {
            return null;
        }
        final Locale locale = LocaleContextHolder.getLocale();
        final Optional<LocalizedTextEntity> localizedText = localizedTextRepository.findByMessageKeyAndLocale(key, locale);
        if (localizedText.isPresent()) {
            return localizedText.get().getText();
        }

        logger.debug("Localized text key: {} not found for locale: {}, falling back to EN", key, locale);
        return localizedTextRepository.findByMessageKeyAndLocale(key, Locale.ENGLISH)
                .map(LocalizedTextEntity::getText)
                .orElse(null);
    }

    private static DefaultArtifactVersion parseVersion(final String version) {
        if (StringUtils.isEmpty(version)) {
            return null;
        }
        return new DefaultArtifactVersion(version);
    }
}
