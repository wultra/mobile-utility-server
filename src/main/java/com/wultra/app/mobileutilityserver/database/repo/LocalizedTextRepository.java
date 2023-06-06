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

package com.wultra.app.mobileutilityserver.database.repo;

import com.wultra.app.mobileutilityserver.database.model.LocalizedTextEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Locale;
import java.util.Optional;

/**
 * Repository for {@link LocalizedTextEntity}.
 *
 * @author Lubos Racansky, lubos.racansky@wultra.com
 */
public interface LocalizedTextRepository extends CrudRepository<LocalizedTextEntity, LocalizedTextEntity.LocalizedTextId> {

    /**
     * Find localized text by given key and locale.
     *
     * @param messageKey Key of the localized text.
     * @param locale Locale to identify language of the text.
     * @return found localized text or empty
     */
    default Optional<LocalizedTextEntity> findByMessageKeyAndLocale(String messageKey, Locale locale) {
        return findById(new LocalizedTextEntity.LocalizedTextId(messageKey, locale.getLanguage()));
    }

}
