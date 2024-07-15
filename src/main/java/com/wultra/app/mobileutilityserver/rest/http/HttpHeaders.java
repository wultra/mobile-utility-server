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
package com.wultra.app.mobileutilityserver.rest.http;

import java.util.Base64;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Class with constants for HTTP request / response headers.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Slf4j
public class HttpHeaders {

    public static final int MIN_CHALLENGE_HEADER_LENGTH = 16;
    public static final int MAX_CHALLENGE_HEADER_LENGTH = 32;

    public static final String REQUEST_CHALLENGE = "X-Cert-Pinning-Challenge";
    public static final String RESPONSE_SIGNATURE = "X-Cert-Pinning-Signature";

    private HttpHeaders() {
        throw new IllegalStateException("Should not be instantiated.");
    }

    public static boolean validChallengeHeader(String challengeHeader) {
        try {
            if (StringUtils.isEmpty(challengeHeader)) {
                logger.warn("Missing or blank challenge header: {}", challengeHeader);
                return false;
            }
            final byte[] challengeBytes = Base64.getDecoder().decode(challengeHeader);
            final int challengeLength = challengeBytes.length;
            if (challengeLength >= MIN_CHALLENGE_HEADER_LENGTH && challengeLength <= MAX_CHALLENGE_HEADER_LENGTH) {
                return true;
            } else {
                logger.warn("Invalid challenge size, must be between {} and {}, was: {}", MIN_CHALLENGE_HEADER_LENGTH, MAX_CHALLENGE_HEADER_LENGTH, challengeLength);
                return false;
            }
        } catch (IllegalArgumentException ex) {
            logger.warn("Invalid Base64 value received in the header: {}", challengeHeader);
            logger.debug("Exception detail: {}", ex.getMessage(), ex);
            return false;
        }
    }

}
