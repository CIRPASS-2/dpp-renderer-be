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
package it.extrared.dpp.renderer.business.search.dto;

import it.extrared.dpp.renderer.business.search.filter.Filter;

/** Dto for a single filter. */
public class FilterDto {

    private String property;

    private FilterOp op;

    private String literal;

    /**
     * Property to evaluate for filter testing.
     *
     * @return the property name.
     */
    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * @return the filter operation to perform.
     */
    public FilterOp getOp() {
        return op;
    }

    public void setOp(FilterOp op) {
        this.op = op;
    }

    /**
     * @return the literal value to test the op.
     */
    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    /**
     * Converts the dto to a {@link Filter}.
     *
     * @return the corresponding {@link Filter}.
     */
    public Filter readFilter() {
        return op.getFilter(getProperty(), getLiteral());
    }
}
