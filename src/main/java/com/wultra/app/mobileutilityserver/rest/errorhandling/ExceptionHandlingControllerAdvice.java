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
package com.wultra.app.mobileutilityserver.rest.errorhandling;

import com.wultra.app.mobileutilityserver.rest.model.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Controller advice responsible for error handling.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@ControllerAdvice
public class ExceptionHandlingControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlingControllerAdvice.class);

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleMissingRequestHeaderException(ServletRequestBindingException ex) {
        String code = "UNKNOWN_ERROR";
        String message = "An error occurred when processing the request.";
        String id = UUID.randomUUID().toString();
        logger.error("Unknown error happened. ID: {}", id, ex);
        return new ErrorResponse(code, message, id);
    }

    @ExceptionHandler(PublicKeyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ErrorResponse handlePublicKeyNotFoundException(PublicKeyNotFoundException ex) {
        String code = "PUBLIC_KEY_NOT_FOUND";
        String message = "Public key for the provided app name was not found.";
        String id = UUID.randomUUID().toString();
        logger.warn("Public key for the provided app name: {} was not found. ID: {}", ex.getAppName(), id);
        return new ErrorResponse(code, message, id);
    }

    @ExceptionHandler(AppNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ErrorResponse handleAppNotFoundException(AppNotFoundException ex) {
        String code = "APP_NOT_FOUND";
        String message = "App with a provided ID was not found.";
        String id = UUID.randomUUID().toString();
        logger.warn("Application for a provided app name: {} was not found. ID: {}", ex.getAppName(), id);
        return new ErrorResponse(code, message, id);
    }

    @ExceptionHandler(InvalidChallengeHeaderException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody ErrorResponse handleInvalidChallengeHeaderException(InvalidChallengeHeaderException ex) {
        String code = "INSUFFICIENT_CHALLENGE";
        String message = "Request does not contain sufficiently strong challenge header, 16B is required at least.";
        String id = UUID.randomUUID().toString();
        logger.error("Request does not contain sufficiently strong challenge header, 16B is required at least. ID: {}", id);
        return new ErrorResponse(code, message, id);
    }
}
