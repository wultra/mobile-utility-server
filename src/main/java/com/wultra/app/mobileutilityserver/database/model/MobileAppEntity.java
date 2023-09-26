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
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a mobile app.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Entity
@Table(name = "mus_mobile_app")
@Getter
@Setter
public class MobileAppEntity {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "mus_mobile_app", sequenceName = "mus_mobile_app_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mus_mobile_app")
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

}
