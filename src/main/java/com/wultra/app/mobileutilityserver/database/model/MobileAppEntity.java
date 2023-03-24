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
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a mobile app.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Entity
@Table(name = "ssl_mobile_app")
public class MobileAppEntity {

    @Id
    @Column(name = "id")
    @GenericGenerator(
            name = "ssl_mobile_app",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "ssl_mobile_app_seq"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    @GeneratedValue(generator = "ssl_mobile_app")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "sign_private_key")
    private String signingPrivateKey;

    @Column(name = "sign_public_key")
    private String signingPublicKey;

    @ToString.Exclude
    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<MobileDomainEntity> domains = new ArrayList<>();

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
     * Get mobile app name.
     * @return App name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set mobile app name.
     * @param name App name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get mobile app display name.
     * @return App display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set mobile app display name.
     * @param displayName App display name.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get app signing private key.
     * @return Signing private key.
     */
    public String getSigningPrivateKey() {
        return signingPrivateKey;
    }

    /**
     * Set app signing private key.
     * @param signingPrivateKey Signing private key.
     */
    public void setSigningPrivateKey(String signingPrivateKey) {
        this.signingPrivateKey = signingPrivateKey;
    }

    /**
     * Get app signing public key.
     * @return Signing public key.
     */
    public String getSigningPublicKey() {
        return signingPublicKey;
    }

    /**
     * Set app signing public key.
     * @param signingPublicKey Signing public key.
     */
    public void setSigningPublicKey(String signingPublicKey) {
        this.signingPublicKey = signingPublicKey;
    }

    /**
     * Get the list of domains.
     * @return Domains.
     */
    public List<MobileDomainEntity> getDomains() {
        return domains;
    }

    /**
     * Set the list of domains.
     * @param domains Domains.
     */
    public void setDomains(List<MobileDomainEntity> domains) {
        this.domains = domains;
    }
}
