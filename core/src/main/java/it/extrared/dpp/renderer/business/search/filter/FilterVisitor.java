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

import java.util.Map;

/** Base interface for a GoF Visitor able to traverse a {@link Filter}. */
public interface FilterVisitor {

    /**
     * Visits a property.
     *
     * @param property the property being visited.
     * @param extraData extraData to give contextual information to the method.
     */
    void visit(Property property, Map<String, Object> extraData);

    /**
     * Visit an Equals filter.
     *
     * @param eq the equals filter being visited.
     * @param extraData extraData to give contextual information to the method.
     */
    void visit(Equals eq, Map<String, Object> extraData);

    /**
     * @param gt the greater than filter being visited.
     * @param extraData extraData to give contextual information to the method.
     */
    void visit(GreaterThan gt, Map<String, Object> extraData);

    /**
     * @param gtEq the greater than equals filter being visited.
     * @param extraData extraData to give contextual information to the method.
     */
    void visit(GreaterThanEq gtEq, Map<String, Object> extraData);

    /**
     * @param lt the less than filter being visited.
     * @param extraData extraData to give contextual information to the method.
     */
    void visit(LowerThan lt, Map<String, Object> extraData);

    /**
     * @param lte the less than equals filter being visited.
     * @param extraData extraData to give contextual information to the method.
     */
    void visit(LowerThanEq lte, Map<String, Object> extraData);

    /**
     * @param like the like filter being visited.
     * @param extraData extraData to give contextual information to the method.
     */
    void visit(Like like, Map<String, Object> extraData);

    /**
     * @param and the and filter being visited.
     * @param extraData extraData to give contextual information to the method.
     */
    void visit(And and, Map<String, Object> extraData);

    /**
     * @param literal the literal value being visited.
     * @param extraData extraData to give contextual information to the method.
     */
    void visit(Literal literal, Map<String, Object> extraData);

    /**
     * @param all the all filter being visited.
     * @param extraData extraData to give contextual information to the method.
     */
    void visit(All all, Map<String, Object> extraData);
}
