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

import javax.persistence.*;

/**
 * Entity representing an SSL pinning fingerprint.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Entity
@Table(name = "ssl_mobile_fingerprint")
public class SslPinningFingerprintDbEntity {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "ssl_mobile_fingerprint", sequenceName = "ssl_mobile_fingerprint_seq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ssl_mobile_fingerprint")
    private Long id;

    @Column(name = "fingerprint")
    private String fingerprint;

    @Column(name = "expires")
    private Long expires;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mobile_domain_id")
    private MobileDomainEntity domain;

    /**
     * Get ID.
     * @return ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set ID.
     * @param id ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get domain certificate fingerprint.
     * @return Certificate fingerprint.
     */
    public String getFingerprint() {
        return fingerprint;
    }

    /**
     * Set domain certificate fingerprint.
     * @param fingerprint Certificate fingerprint.
     */
    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    /**
     * Get expiration timestamp.
     * @return Expiration timestamp.
     */
    public Long getExpires() {
        return expires;
    }

    /**
     * Set expiration timestamp.
     * @param expires Expiration timestamp.
     */
    public void setExpires(Long expires) {
        this.expires = expires;
    }

    /**
     * Get associated domain.
     * @return Domain.
     */
    public MobileDomainEntity getDomain() {
        return domain;
    }

    /**
     * Set associated domain.
     * @param domain Domain.
     */
    public void setDomain(MobileDomainEntity domain) {
        this.domain = domain;
    }
}
