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

package com.wultra.app.mobileutilityserver.database.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


/**
 * Entity representing an SSL pinning certificate.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Entity
@Table(name = "mus_certificate")
@Getter
@Setter
public class CertificateEntity {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "mus_certificate", sequenceName = "mus_certificate_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mus_certificate")
    private Long id;

    @Column(name = "pem")
    private String pem;

    @Column(name = "fingerprint")
    private String fingerprint;

    @Column(name = "expires")
    private Long expires;

    @Column(name = "depth")
    private Integer depth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mobile_domain_id")
    private MobileDomainEntity domain;

}
