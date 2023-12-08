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
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * Entity representing localized text.
 *
 * @author Lubos Racansky, lubos.racansky@wultra.com
 */
@Entity
@Table(name = "mus_localized_text")
@Getter
@Setter
@ToString
@IdClass(LocalizedTextEntity.LocalizedTextId.class)
public class LocalizedTextEntity {

    @Id
    private String messageKey;

    /**
     * ISO 639-1 two-letter language code.
     */
    @Id
    private String language;

    @Column(nullable = false)
    private String text;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    public static class LocalizedTextId implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String messageKey;

        /**
         * ISO 639-1 two-letter language code.
         */
        private String language;
    }

}
