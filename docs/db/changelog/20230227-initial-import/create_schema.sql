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

CREATE SEQUENCE IF NOT EXISTS ssl_mobile_app_seq MAXVALUE 9999999999999 CACHE 20;
CREATE SEQUENCE IF NOT EXISTS ssl_mobile_domain_seq MAXVALUE 9999999999999 CACHE 20;
CREATE SEQUENCE IF NOT EXISTS ssl_mobile_fingerprint_seq MAXVALUE 9999999999999 CACHE 20;

CREATE TABLE IF NOT EXISTS ssl_mobile_app (
    id                  INTEGER PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    display_name        VARCHAR(255) NOT NULL,
    sign_private_key    VARCHAR(255) NOT NULL,
    sign_public_key     VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS ssl_mobile_domain (
    id                  INTEGER PRIMARY KEY,
    app_id              INTEGER NOT NULL
        CONSTRAINT ssl_mobile_domain_fk
            REFERENCES ssl_mobile_app ON UPDATE CASCADE ON DELETE CASCADE,
    domain              VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS ssl_mobile_fingerprint (
    id                  INTEGER PRIMARY KEY,
    fingerprint         VARCHAR(255) NOT NULL,
    expires             INTEGER NOT NULL,
    mobile_domain_id    INTEGER NOT NULL
        CONSTRAINT mobile_ssl_pinning_app_fk
            REFERENCES ssl_mobile_domain ON UPDATE CASCADE ON DELETE CASCADE
);
