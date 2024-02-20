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
package com.wultra.app.mobileutilityserver.rest.filter;

import com.wultra.app.mobileutilityserver.rest.http.HttpHeaders;
import com.wultra.app.mobileutilityserver.rest.http.QueryParams;
import com.wultra.app.mobileutilityserver.rest.service.CryptographicOperationsService;
import com.wultra.app.mobileutilityserver.rest.service.MobileAppService;
import io.getlime.security.powerauth.crypto.lib.model.exception.CryptoProviderException;
import io.getlime.security.powerauth.crypto.lib.model.exception.GenericCryptoException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;

/**
 * Filter that signs the response data with a signature that depends on the received challenge.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Component
@Slf4j
public class ResponseSignFilter extends OncePerRequestFilter {

    private static final String PATH_PREFIX = "/app/init";

    private final MobileAppService mobileAppService;

    private final CryptographicOperationsService cryptographicOperationsService;

    @Autowired
    public ResponseSignFilter(MobileAppService mobileAppService, CryptographicOperationsService cryptographicOperationsService) {
        this.mobileAppService = mobileAppService;
        this.cryptographicOperationsService = cryptographicOperationsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        final String url = request.getRequestURI();
        return !url.startsWith(PATH_PREFIX);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
        final ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        chain.doFilter(request, responseWrapper);
        final String requestChallenge = request.getHeader(HttpHeaders.REQUEST_CHALLENGE);
        if (HttpHeaders.validChallengeHeader(requestChallenge)) {
            // Prepare the signature base data
            final byte[] cb = requestChallenge.getBytes(StandardCharsets.UTF_8);
            final byte[] rb = responseWrapper.getContentAsByteArray();

            final byte[] signatureBase = new byte[cb.length + rb.length + 1];
            System.arraycopy(cb, 0, signatureBase, 0, cb.length);
            signatureBase[cb.length] = '&';
            System.arraycopy(rb, 0, signatureBase, cb.length + 1, rb.length);

            // Fetch the app private key, check if such app exists
            final String appName = request.getParameter(QueryParams.QUERY_PARAM_APP_NAME);
            final String privateKeyBase64 = mobileAppService.privateKey(appName);

            if (privateKeyBase64 != null) {
                try {
                    // Compute the signature
                    final String ecdsaSignature = cryptographicOperationsService.computeECDSASignature(signatureBase, privateKeyBase64);

                    // Set the request header
                    response.setHeader(HttpHeaders.RESPONSE_SIGNATURE, ecdsaSignature);
                } catch (InvalidKeySpecException | CryptoProviderException | InvalidKeyException | GenericCryptoException ex) {
                    logger.error("Unable to sign response, appName: {}", appName, ex);
                    throw new IOException("Unable to sign response, appName: " + appName, ex);
                }
            }
        }
        responseWrapper.copyBodyToResponse();
    }
}
