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

import com.wultra.app.mobileutilityserver.database.model.MobileAppEntity;
import com.wultra.app.mobileutilityserver.database.repo.MobileAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service acting as a DAO for mobile application related objects.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Service
public class MobileAppService {

    private final MobileAppRepository repo;

    @Autowired
    public MobileAppService(MobileAppRepository repo) {
        this.repo = repo;
    }

    /**
     * Checks if an app with a provided name exists.
     * @param appName App name.
     * @return True in case the app with given name exists, false otherwise.
     */
    public boolean appExists(String appName) {
        return repo.existsByName(appName);
    }

    /**
     * Return a private key (Base64 encoded value) of an app with provided app name.
     *
     * @param appName App name.
     * @return Private key encoded as Base64 representation of the embedded big integer, or null
     * if app with provided name does not exist.
     */
    public String privateKey(String appName) {
        final MobileAppEntity mobileAppEntity = repo.findFirstByName(appName);
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
        final MobileAppEntity mobileAppEntity = repo.findFirstByName(appName);
        if (mobileAppEntity == null) {
            return null;
        }
        return mobileAppEntity.getSigningPublicKey();
    }

}
