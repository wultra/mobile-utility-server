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

create sequence hibernate_sequence maxvalue 9999999999999 cache 20;

create table mobile_app (
    id                  integer not null primary key,
    name                varchar(255),
    display_name        varchar(255),
    sign_private_key    varchar(255),
    sign_public_key     varchar(255)
);

create table mobile_domain (
    id                  integer not null primary key,
    app_id integer      not null,
    domain varchar(255) not null
);

create table mobile_ssl_pinning
(
    id                  integer not null primary key,
    fingerprint         varchar(255) not null,
    expires             integer,
    mobile_domain_id    integer not null
        constraint mobile_ssl_pinning_app_fk references mobile_domain on update cascade on delete cascade
);
