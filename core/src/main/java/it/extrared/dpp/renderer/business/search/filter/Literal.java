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
package it.extrared.dpp.renderer.business.search.filter;

import static it.extrared.dpp.renderer.utils.CommonUtils.unwrapWhiteSpace;

import java.util.Map;

/**
 * Represent a literal value. If a literal value is always provided as a string it is converted to
 * the proper type assuming that text values are always between single quote ('text'), decimal
 * values are separated by a point (es. 1.2) and true | false strings are interpreted as boolean.
 * There is currently no support for dates and date times.
 */
public class Literal implements Filter {

    private static final String STR_LITERAL = "'.*?'";

    private static final String BOOLEAN_LITERAL = "true|false";

    private static final String DOUBLE_LITERAL = "-?\\d+(\\.\\d+)?";

    private static final String INT_LITERAL = "-?\\d+";

    private final Object value;

    public Literal(String rawLiteral) {
        this.value = parseLiteral(rawLiteral);
    }

    public Literal(Object value) {
        this.value = value;
    }

    private Object parseLiteral(String value) {
        Object result = null;
        value = unwrapWhiteSpace(value);
        if (value.matches(BOOLEAN_LITERAL)) {
            result = Boolean.valueOf(value);
        } else if (value.matches(INT_LITERAL)) {
            Long temp = Long.valueOf(value);
            if (temp <= Integer.MAX_VALUE) result = temp.intValue();
            else result = temp;
        } else if (value.matches(DOUBLE_LITERAL)) {
            result = Double.valueOf(value);
        } else if (value.matches(STR_LITERAL)) {
            result = value.length() > 2 ? value.substring(1, value.length() - 1) : "";
        } else {
            throw new RuntimeException("Unable to parse literal %s".formatted(value));
        }
        return result;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public void accept(FilterVisitor visitor, Map<String, Object> extradata) {
        visitor.visit(this, extradata);
    }
}
