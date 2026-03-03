/*
 * Copyright 2024-2027 CIRPASS-2
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.extrared.dpp.renderer.mariadb;

import it.extrared.dpp.renderer.business.search.filter.Property;
import it.extrared.dpp.renderer.business.search.filter.SQLFilterVisitor;
import org.jboss.logging.Logger;

import java.util.Map;

import static it.extrared.dpp.renderer.utils.CommonUtils.debug;

/**
 * An implementation of a {@link SQLFilterVisitor} supporting MariaDB SQL dialect.
 */
public class MariaDBFilterVisitor extends SQLFilterVisitor {
    private static final Logger LOGGER= Logger.getLogger(MariaDBFilterVisitor.class);
    @Override
    public void visit(Property property, Map<String, Object> extraData) {
        String field;
        if (isColumnField(property.getPropertyName())) {
            debug(()->"Field %s is a column field".formatted(property.getPropertyName()),LOGGER);
            field = COLUMNS.get(property.getPropertyName());
        } else {
            debug(()->"Field %s is a JSON field converting to JSON fragment ".formatted(property.getPropertyName()),LOGGER);
            field = "JSON_EXTRACT(search_data, '$.%s')".formatted(property.getPropertyName());
            debug(()->"Converted to %s".formatted(property.getPropertyName()),LOGGER);
        }
        if (isLike(extraData)) queryConditions.append(" UPPER(%s) ".formatted(field));
        else queryConditions.append(" %s ".formatted(field));
    }
}
