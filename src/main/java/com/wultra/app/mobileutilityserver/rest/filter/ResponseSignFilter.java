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

import com.google.common.io.BaseEncoding;
import com.wultra.app.mobileutilityserver.rest.http.HttpHeaders;
import com.wultra.app.mobileutilityserver.rest.http.QueryParams;
import com.wultra.app.mobileutilityserver.rest.service.MobileAppDAO;
import io.getlime.security.powerauth.crypto.lib.model.exception.CryptoProviderException;
import io.getlime.security.powerauth.crypto.lib.model.exception.GenericCryptoException;
import io.getlime.security.powerauth.crypto.lib.util.KeyConvertor;
import io.getlime.security.powerauth.crypto.lib.util.SignatureUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

/**
 * Filter that signs the response data with a signature that depends on the received challenge.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Component
public class ResponseSignFilter extends OncePerRequestFilter {

    private static final int MIN_CHALLENGE_LENGTH = 16;
    private final MobileAppDAO mobileAppService;
    private final KeyConvertor keyConvertor;
    private final SignatureUtils signatureUtils;

    @Autowired
    public ResponseSignFilter(MobileAppDAO mobileAppService, KeyConvertor keyConvertor, SignatureUtils signatureUtils) {
        this.mobileAppService = mobileAppService;
        this.keyConvertor = keyConvertor;
        this.signatureUtils = signatureUtils;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
        final ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        chain.doFilter(request, responseWrapper);
        String requestChallenge = request.getHeader(HttpHeaders.REQUEST_CHALLENGE);
        if (requestChallenge != null
                && !requestChallenge.isEmpty()
                && !requestChallenge.isBlank()
                && requestChallenge.length() >= MIN_CHALLENGE_LENGTH) {
            try {

                // Prepare the signature base data
                byte[] cb = requestChallenge.getBytes(StandardCharsets.UTF_8);
                byte[] rb = responseWrapper.getContentAsByteArray();

                ByteArrayOutputStream baos = new ByteArrayOutputStream( );
                baos.write(cb);
                baos.write('&');
                baos.write(rb);

                byte[] signatureBase = baos.toByteArray();

                // Fetch the app private key, check if such app exists
                final String appName = request.getParameter(QueryParams.QUERY_PARAM_APP_NAME);
                final String privateKeyBase64 = mobileAppService.privateKey(appName);
                if (privateKeyBase64 != null) {
                    // Convert the private key
                    final PrivateKey privateKey = keyConvertor.convertBytesToPrivateKey(BaseEncoding.base64().decode(privateKeyBase64));

                    // Compute the signature
                    final byte[] ecdsaSignature = signatureUtils.computeECDSASignature(signatureBase, privateKey);

                    // Set the request header
                    response.setHeader(HttpHeaders.RESPONSE_SIGNATURE, BaseEncoding.base64().encode(ecdsaSignature));
                }
            } catch (InvalidKeySpecException | CryptoProviderException | InvalidKeyException | GenericCryptoException ex) {
                throw new IOException(ex);
            }

        }
        responseWrapper.copyBodyToResponse();
    }
}
