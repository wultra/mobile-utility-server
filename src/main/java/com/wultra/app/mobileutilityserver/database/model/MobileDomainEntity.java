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

package com.wultra.app.mobileutilityserver.database.model;

import javax.persistence.*;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing an internet domain.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Entity
@Table(name = "mus_mobile_domain")
public class MobileDomainEntity {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "mus_mobile_domain", sequenceName = "mus_mobile_domain_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mus_mobile_domain")
    private Long id;

    @Column(name = "domain")
    private String domain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id")
    private MobileAppEntity app;

    @ToString.Exclude
    @OneToMany(mappedBy = "domain", cascade = CascadeType.ALL, orphanRemoval=true)
    private final List<CertificateEntity> certificates = new ArrayList<>();

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
     * Get domain common name.
     * @return Domain common name.
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Set domain common name.
     * @param domain Common name.
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Get related mobile application.
     * @return Mobile application.
     */
    public MobileAppEntity getApp() {
        return app;
    }

    /**
     * Set related mobile application.
     * @param app Mobile application.
     */
    public void setApp(MobileAppEntity app) {
        this.app = app;
    }

    public List<CertificateEntity> getCertificates() {
        return certificates;
    }
}
