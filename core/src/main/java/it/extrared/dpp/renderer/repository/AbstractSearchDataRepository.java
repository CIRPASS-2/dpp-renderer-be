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
package it.extrared.dpp.renderer.repository;

import static it.extrared.dpp.renderer.utils.CommonUtils.debug;

import com.fasterxml.jackson.databind.JsonNode;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.*;
import it.extrared.dpp.renderer.business.search.filter.Filter;
import it.extrared.dpp.renderer.business.search.filter.SQLFilterVisitor;
import it.extrared.dpp.renderer.dto.SearchDataDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jboss.logging.Logger;

/**
 * Abstract implementation of a {@link SearchDataRepository}. Provides reusable logic for actual
 * implementations.
 */
public abstract class AbstractSearchDataRepository implements SearchDataRepository {

    private static final Logger LOGGER = Logger.getLogger(AbstractSearchDataRepository.class);
    protected static final BiFunction<Row, Function<Row, JsonNode>, SearchDataDto> MAPPER =
            (r, f) -> {
                SearchDataDto searchDataDto = new SearchDataDto();
                searchDataDto.setId(r.getLong("id"));
                searchDataDto.setUpi(r.getString("upi"));
                searchDataDto.setLiveUrl(r.getString("live_url"));
                searchDataDto.setData(f.apply(r));
                return searchDataDto;
            };

    @Override
    public Uni<Long> count(SqlConnection conn, Filter filter) {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) from dpp_data ");
        return executeWithFilter(query, conn, filter, null, null)
                .map(RowSet::iterator)
                .map(it -> it.hasNext() ? it.next().getLong(0) : 0L);
    }

    @Override
    public Uni<List<SearchDataDto>> search(
            SqlConnection conn, Filter filter, Integer offset, Integer limit) {
        StringBuilder query = new StringBuilder("SELECT * from dpp_data ");
        return executeWithFilter(query, conn, filter, offset, limit)
                .map(rs -> rs.stream().map(r -> MAPPER.apply(r, jsonMapper())).toList());
    }

    protected Uni<RowSet<Row>> executeWithFilter(
            StringBuilder query, SqlConnection conn, Filter filter, Integer offset, Integer limit) {
        LinkedList<Object> params = null;
        if (filter != null) {
            debug(() -> "Filter not null converting to sql query", LOGGER);
            SQLFilterVisitor visitor = getFilterVisitor();
            filter.accept(visitor, new HashMap<>());
            StringBuilder condition = visitor.getCondition();
            debug(() -> "Query is %s".formatted(condition), LOGGER);
            params = visitor.getParams();
            if (!condition.isEmpty()) query.append(" WHERE ").append(condition);
        }
        if (offset != null && limit != null)
            query.append(" Order by id asc limit %s offset %s".formatted(limit, offset));
        String strQ = query.toString();
        debug(() -> "Full query is %s".formatted(strQ), LOGGER);
        PreparedQuery<RowSet<Row>> pquery = conn.preparedQuery(strQ);
        Uni<RowSet<Row>> rowSetUni;
        if (params != null && !params.isEmpty()) {
            if (LOGGER.isDebugEnabled()) logParams(params);
            rowSetUni = pquery.execute(Tuple.tuple(params));
        } else rowSetUni = pquery.execute();
        return rowSetUni;
    }

    private void logParams(LinkedList<Object> params) {
        List<String> str = new ArrayList<>();
        for (int i = 0; i < params.size(); i++) {
            str.add("param %s is %s".formatted(i + 1, params.get(i)));
        }
        debug(() -> String.join(",", str), LOGGER);
    }

    protected abstract Function<Row, JsonNode> jsonMapper();

    protected abstract SQLFilterVisitor getFilterVisitor();
}
