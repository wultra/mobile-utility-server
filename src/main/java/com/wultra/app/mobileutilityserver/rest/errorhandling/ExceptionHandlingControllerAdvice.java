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

import com.wultra.app.mobileutilityserver.rest.model.errors.ExtendedError;
import com.wultra.app.mobileutilityserver.rest.model.errors.Violation;
import io.getlime.core.rest.model.base.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

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
        final String code = "UNKNOWN_ERROR";
        final String message = "An error occurred when processing the request.";
        logger.error("Unknown error happened: {}", ex.getMessage());
        logger.debug("Exception detail: ", ex);
        return new ErrorResponse(code, message);
    }

    @ExceptionHandler(PublicKeyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ErrorResponse handlePublicKeyNotFoundException(PublicKeyNotFoundException ex) {
        final String code = "PUBLIC_KEY_NOT_FOUND";
        final String message = "Public key for the provided app name was not found.";
        logger.warn("Public key for the provided app name: {} was not found: {}", ex.getAppName(), ex.getMessage());
        logger.debug("Exception detail: ", ex);
        return new ErrorResponse(code, message);
    }

    @ExceptionHandler(AppException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleAppException(AppException ex) {
        final String code = "APP_EXCEPTION";
        final String message = ex.getMessage();
        logger.warn("Problem occurred while working with applications: {}", ex.getMessage());
        logger.debug("Exception detail: ", ex);
        return new ErrorResponse(code, message);
    }

    @ExceptionHandler(AppNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ErrorResponse handleAppNotFoundException(AppNotFoundException ex) {
        final String code = "APP_NOT_FOUND";
        final String message = "App with a provided ID was not found.";
        logger.warn("Application for a provided app name: {} was not found: {}", ex.getAppName(), ex.getMessage());
        logger.debug("Exception detail: ", ex);
        return new ErrorResponse(code, message);
    }

    @ExceptionHandler(InvalidChallengeHeaderException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody ErrorResponse handleInvalidChallengeHeaderException(InvalidChallengeHeaderException ex) {
        final String code = "INSUFFICIENT_CHALLENGE";
        final String message = "Request does not contain sufficiently strong challenge header, 16B is required at least.";
        logger.error("Request does not contain sufficiently strong challenge header, 16B is required at least: {}", ex.getMessage());
        logger.debug("Exception detail: ", ex);
        return new ErrorResponse(code, message);
    }

    // Standard Spring Exceptions

    /**
     * Exception handler for issues related to failed argument validations.
     *
     * @param e Exception.
     * @return Response with error details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody io.getlime.core.rest.model.base.response.ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.warn("Error occurred when calling an API: {}", e.getMessage());
        logger.debug("Exception detail: ", e);
        final ExtendedError error = new ExtendedError("ERROR_REQUEST", "Invalid method parameter value");
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            error.getViolations().add(
                    new Violation(fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage())
            );
        }
        return new io.getlime.core.rest.model.base.response.ErrorResponse(error);
    }

    /**
     * Exception handler for issues related to failed argument validations.
     *
     * @param e Exception.
     * @return Response with error details.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody io.getlime.core.rest.model.base.response.ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        logger.warn("Error occurred when calling an API: {}", e.getMessage());
        logger.debug("Exception detail: ", e);
        final ExtendedError error = new ExtendedError("ERROR_REQUEST", "Invalid object value");
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            error.getViolations().add(
                    new Violation(violation.getPropertyPath().toString(), violation.getInvalidValue(), violation.getMessage())
            );
        }
        return new io.getlime.core.rest.model.base.response.ErrorResponse(error);
    }

    /**
     * Exception handler for issues related to missing mandatory attribute.
     *
     * @param e Exception.
     * @return Response with error details.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody io.getlime.core.rest.model.base.response.ErrorResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        logger.warn("Error occurred when calling an API: {}", e.getMessage());
        logger.debug("Exception detail: ", e);
        return new io.getlime.core.rest.model.base.response.ErrorResponse("ERROR_REQUEST", e.getMessage());
    }

    /**
     * Exception handler for issues related to invalid HTTP media type.
     *
     * @param e Exception.
     * @return Response with error details.
     */
    @ExceptionHandler(HttpMediaTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody io.getlime.core.rest.model.base.response.ErrorResponse handleHttpMediaTypeException(HttpMediaTypeException e) {
        logger.warn("Error occurred when calling an API: {}", e.getMessage());
        logger.debug("Exception detail: ", e);
        return new io.getlime.core.rest.model.base.response.ErrorResponse("ERROR_REQUEST", e.getMessage());
    }

    /**
     * Exception handler for issues related to malicious requests.
     *
     * @param e Exception.
     * @return Response with error details.
     */
    @ExceptionHandler(RequestRejectedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody io.getlime.core.rest.model.base.response.ErrorResponse handleRequestRejectedException(RequestRejectedException e) {
        logger.warn("Error occurred when calling an API: {}", e.getMessage());
        logger.debug("Exception detail: ", e);
        return new io.getlime.core.rest.model.base.response.ErrorResponse("ERROR_REQUEST", e.getMessage());
    }

    /**
     * Exception handler for issues related to invalid HTTP method.
     *
     * @param e Exception.
     * @return Response with error details.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody io.getlime.core.rest.model.base.response.ErrorResponse handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        logger.warn("Error occurred when calling an API: {}", e.getMessage());
        logger.debug("Exception detail: ", e);
        return new io.getlime.core.rest.model.base.response.ErrorResponse("ERROR_REQUEST", e.getMessage());
    }

    /**
     * Exception handler for issues related to invalid mappings.
     *
     * @param e Exception.
     * @return Response with error details.
     */
    @ExceptionHandler(HttpMessageConversionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody io.getlime.core.rest.model.base.response.ErrorResponse handleHttpMessageConversionException(HttpMessageConversionException e) {
        logger.warn("Error occurred when calling an API: {}", e.getMessage());
        logger.debug("Exception detail: ", e);
        return new io.getlime.core.rest.model.base.response.ErrorResponse("ERROR_REQUEST", "Unable to map request data. Check the JSON payload.");
    }

    /**
     * Exception handler for authentication and role issues.
     *
     * @param e Exception.
     * @return Response with error details.
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody io.getlime.core.rest.model.base.response.ErrorResponse handleAccessDeniedException(AccessDeniedException e) {
        logger.warn("Error occurred when calling an API: {}", e.getMessage());
        logger.debug("Exception detail: ", e);
        return new io.getlime.core.rest.model.base.response.ErrorResponse("ERROR_AUTHENTICATION", e.getMessage());
    }
}
