/*
 * Wultra Mobile Utility Server
 * Copyright (C) 2026 Wultra s.r.o.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.wultra.app.mobileutilityserver.rest.errorhandling;


import lombok.Getter;

import java.io.Serial;

/**
 * Exception indicating a domain name does not match the certificate.
 */
public class DomainNameCertificateMismatchException extends Exception {

    @Serial
    private static final long serialVersionUID = -5595210772649299223L;

    @Getter
    private final String domainName;

    public DomainNameCertificateMismatchException(final String domainName, final Throwable cause) {
        super("Domain name <%s> does not match the provided certificate.".formatted(domainName), cause);
        this.domainName = domainName;
    }

    public DomainNameCertificateMismatchException(final String domainName, final String message, final Throwable cause) {
        super(message, cause);
        this.domainName = domainName;
    }
}
