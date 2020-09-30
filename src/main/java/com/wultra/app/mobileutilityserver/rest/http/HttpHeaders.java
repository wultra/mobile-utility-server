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

/**
 * Class with constants for HTTP request / response headers.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
public class HttpHeaders {

    public static final int MIN_CHALLENGE_HEADER_LENGTH = 16;

    public static final String REQUEST_CHALLENGE = "X-Cert-Pinning-Challenge";
    public static final String RESPONSE_SIGNATURE = "X-Cert-Pinning-Signature";

    public static boolean validChallengeHeader(String challengeHeader) {
        return challengeHeader != null
                && !challengeHeader.isEmpty()
                && !challengeHeader.isBlank()
                && challengeHeader.length() >= HttpHeaders.MIN_CHALLENGE_HEADER_LENGTH;
    }

}
