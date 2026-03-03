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
package it.extrared.dpp.renderer.pgsql;

import static it.extrared.dpp.renderer.utils.CommonUtils.debug;

import it.extrared.dpp.renderer.business.search.filter.Property;
import it.extrared.dpp.renderer.business.search.filter.SQLFilterVisitor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jboss.logging.Logger;

/** An implementation of a {@link SQLFilterVisitor} supporting PgSQL dialect. */
public class PgSQLFilterVisitor extends SQLFilterVisitor {

    private static final Map<Class<?>, String> PG_TYPE_MAP = new LinkedHashMap<>();

    private static final Logger LOGGER = Logger.getLogger(PgSQLFilterVisitor.class);

    static {
        PG_TYPE_MAP.put(Short.class, "smallint");
        PG_TYPE_MAP.put(short.class, "smallint");
        PG_TYPE_MAP.put(Integer.class, "integer");
        PG_TYPE_MAP.put(int.class, "integer");
        PG_TYPE_MAP.put(Long.class, "bigint");
        PG_TYPE_MAP.put(long.class, "bigint");
        PG_TYPE_MAP.put(BigInteger.class, "numeric");
        PG_TYPE_MAP.put(Float.class, "real");
        PG_TYPE_MAP.put(float.class, "real");
        PG_TYPE_MAP.put(Double.class, "double precision");
        PG_TYPE_MAP.put(double.class, "double precision");
        PG_TYPE_MAP.put(BigDecimal.class, "numeric");
        PG_TYPE_MAP.put(Boolean.class, "boolean");
        PG_TYPE_MAP.put(boolean.class, "boolean");
        PG_TYPE_MAP.put(String.class, null);
    }

    private Integer index = 1;

    @Override
    public void visit(Property property, Map<String, Object> extraData) {
        String field;
        if (isColumnField(property.getPropertyName())) {
            debug(
                    () -> "Property %s is a column field ".formatted(property.getPropertyName()),
                    LOGGER);
            field = COLUMNS.get(property.getPropertyName());
        } else {
            debug(() -> "Property is a JSON field. Converting it to JSON fragment", LOGGER);
            Class<?> literalType = null;
            if (extraData.containsKey(LITERAL_TYPE)) {
                literalType = (Class<?>) extraData.get(LITERAL_TYPE);
            } else {
                literalType = String.class;
            }
            field = jsonFieldFragment(property.getPropertyName(), literalType);
            debug(() -> "Json fragment is %s".formatted(field), LOGGER);
        }
        if (isLike(extraData)) queryConditions.append(" UPPER(%s) ".formatted(field));
        else queryConditions.append(" ").append(field).append(" ");
    }

    private String jsonFieldFragment(String property, Class<?> type) {
        if (type == null || !PG_TYPE_MAP.containsKey(type)) {
            return resolveUnknown(type).formatted(property);
        }
        String pgType;
        if (!Number.class.isAssignableFrom(type)) pgType = PG_TYPE_MAP.get(type);
        else pgType = "numeric";

        return pgType == null
                ? "search_data->>'%s'".formatted(property)
                : "(search_data->>'%s')::%s".formatted(property, pgType);
    }

    private String resolveUnknown(Class<?> type) {
        if (type == null) return "search_data->>'%s'";
        if (type.isEnum()) {
            return "search_data->>'%s'";
        }
        if (Number.class.isAssignableFrom(type)) {
            return "(search_data->>'%s')::numeric";
        }
        return "search_data->>'%s'";
    }

    @Override
    protected String getPlaceholder() {
        return "$" + index++;
    }
}
