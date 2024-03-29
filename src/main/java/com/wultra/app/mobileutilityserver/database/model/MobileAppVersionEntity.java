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

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a mobile app required versions.
 *
 * @author Lubos Racansky, lubos.racansky@wultra.com
 */
@Entity
@Table(name = "mus_mobile_app_version")
@Getter
@Setter
@ToString
public class MobileAppVersionEntity {

    @Id
    @SequenceGenerator(name = "mus_mobile_app_version", sequenceName = "mus_mobile_app_version_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mus_mobile_app_version")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", nullable = false)
    private MobileAppEntity app;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false)
    private Platform platform;

    /**
     * Major operation system version, may be {@code null} to match all.
     * <p>
     * For iOS e.g. 12.4.2 it is 12. For Android, it is API level e.g. 29.
     */
    @Column(name = "major_os_version")
    private Integer majorOsVersion;

    @Column(name = "suggested_version")
    private String suggestedVersion;

    @Column(name = "required_version")
    private String requiredVersion;

    @Column(name = "message_key")
    private String messageKey;

    public enum Platform {
        ANDROID,
        IOS
    }
}
