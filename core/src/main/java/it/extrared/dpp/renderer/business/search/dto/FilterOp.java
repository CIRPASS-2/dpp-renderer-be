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

import it.extrared.dpp.renderer.business.search.filter.*;
import java.util.function.BiFunction;

/** Enumeration of supported filter operation. */
public enum FilterOp {
    EQ((left, right) -> new Equals(new Property(left), new Literal(right))),
    GTE((left, right) -> new GreaterThanEq(new Property(left), new Literal(right))),
    LTE((left, right) -> new LowerThanEq(new Property(left), new Literal(right))),
    GT((left, right) -> new GreaterThan(new Property(left), new Literal(right))),
    LT((left, right) -> new LowerThan(new Property(left), new Literal(right))),
    LIKE((left, right) -> new Like(new Property(left), new Literal(right)));

    private final BiFunction<String, String, BiFilter> filterFactory;

    FilterOp(BiFunction<String, String, BiFilter> filterFactory) {
        this.filterFactory = filterFactory;
    }

    /**
     * Given a property and a literal create a filter out of a FilterOp instance.
     *
     * @param property the property to evaluate.
     * @param literal the literal to test the operation.
     * @return the {@link Filter} corresponding to the operation and the parameters.
     */
    public Filter getFilter(String property, String literal) {
        return this.filterFactory.apply(property, literal);
    }
}
