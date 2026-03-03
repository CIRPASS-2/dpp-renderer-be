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

import static it.extrared.dpp.renderer.utils.CommonUtils.debug;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.jboss.logging.Logger;

/**
 * Abstract implementation of a {@link FilterVisitor} producing a SQL where condition from a {@link
 * Filter}
 */
public abstract class SQLFilterVisitor implements FilterVisitor {

    protected static final Map<String, String> COLUMNS =
            Map.of("upi", "upi", "liveUrl", "live_url");

    protected static final String JSON_COLUMN = "search_data";

    protected static final String IS_LIKE = "LIKE";
    protected StringBuilder queryConditions = new StringBuilder();

    protected static final String LITERAL_TYPE = "literalType";
    protected LinkedList<Object> params = new LinkedList<>();
    private static final Logger LOGGER = Logger.getLogger(SQLFilterVisitor.class);

    @Override
    public void visit(Equals eq, Map<String, Object> extraData) {
        extraData = addLiteralKey(eq, extraData);
        eq.getLeft().accept(this, extraData);
        queryConditions.append(" = ");
        eq.getRight().accept(this, extraData);
    }

    @Override
    public void visit(GreaterThan gt, Map<String, Object> extraData) {
        extraData = addLiteralKey(gt, extraData);
        gt.getLeft().accept(this, extraData);
        queryConditions.append(" > ");
        gt.getRight().accept(this, extraData);
    }

    @Override
    public void visit(GreaterThanEq gtEq, Map<String, Object> extraData) {
        extraData = addLiteralKey(gtEq, extraData);
        gtEq.getLeft().accept(this, extraData);
        queryConditions.append(" >= ");
        gtEq.getRight().accept(this, extraData);
    }

    @Override
    public void visit(LowerThan lt, Map<String, Object> extraData) {
        extraData = addLiteralKey(lt, extraData);
        lt.getLeft().accept(this, extraData);
        queryConditions.append(" < ");
        lt.getRight().accept(this, extraData);
    }

    @Override
    public void visit(LowerThanEq lte, Map<String, Object> extraData) {
        extraData = addLiteralKey(lte, extraData);
        lte.getLeft().accept(this, extraData);
        queryConditions.append(" <= ");
        lte.getRight().accept(this, extraData);
    }

    @Override
    public void visit(Like like, Map<String, Object> extraData) {
        if (extraData == null) extraData = new HashMap<>();
        extraData = addLiteralKey(like, extraData);
        extraData.put(IS_LIKE, true);
        like.getLeft().accept(this, extraData);
        queryConditions.append(" LIKE ");
        like.getRight().accept(this, extraData);
    }

    @Override
    public void visit(And and, Map<String, Object> extraData) {
        Filter[] filters = and.getFilters();
        for (int i = 0; i < filters.length; i++) {
            if (i > 0) queryConditions.append(" AND ");
            filters[i].accept(this, extraData);
        }
    }

    @Override
    public void visit(Literal literal, Map<String, Object> extraData) {
        debug(() -> "Literal value is %s".formatted(literal.getValue()), LOGGER);
        Object object = literal.getValue();
        if (isLike(extraData)) {
            debug(() -> "Like operation fixing literal param", LOGGER);
            object = "%" + object.toString().toUpperCase() + "%";
        }
        this.params.addLast(object);
        this.queryConditions.append(" ").append(getPlaceholder()).append(" ");
    }

    /**
     * Returns the placeholder character for a query. It might depends on the SQL dialect.
     * Subclasses must override accordingly.
     *
     * @return the placeholder character as String
     */
    protected String getPlaceholder() {
        return "?";
    }

    @Override
    public void visit(All all, Map<String, Object> extraData) {}

    /**
     * Return the list of params to be passed to a query.
     *
     * @return the list of params.
     */
    public LinkedList<Object> getParams() {
        return params;
    }

    /**
     * Provide the query condition produced from a Filter. Must be called after a filter has been
     * visited.
     *
     * @return the sql query condition as a StringBuilder.
     */
    public StringBuilder getCondition() {
        return queryConditions;
    }

    /**
     * Check if a field is a DB column name.
     *
     * @param field the field name
     * @return true if the field name is a column name false otherwise.
     */
    protected boolean isColumnField(String field) {
        return COLUMNS.containsKey(field);
    }

    private Map<String, Object> addLiteralKey(BiFilter biFilter, Map<String, Object> extraData) {
        Literal literal = biFilter.getLiteral();
        if (extraData == null) extraData = new HashMap<>();
        if (literal != null) {
            extraData.put(
                    LITERAL_TYPE,
                    literal.getValue() != null ? literal.getValue().getClass() : null);
        }
        return extraData;
    }

    /**
     * @param extraData the map containing contextual information.
     * @return true if the extraData map contains the flag labelling the current visited filter as a
     *     Like.
     */
    protected boolean isLike(Map<String, Object> extraData) {
        return extraData.containsKey(IS_LIKE)
                && extraData.get(IS_LIKE) != null
                && (Boolean) extraData.get(IS_LIKE);
    }
}
