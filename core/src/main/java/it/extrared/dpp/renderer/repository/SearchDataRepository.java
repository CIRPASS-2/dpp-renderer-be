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

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlConnection;
import it.extrared.dpp.renderer.business.search.filter.Filter;
import it.extrared.dpp.renderer.dto.SearchDataDto;
import java.util.List;

/** Base repository to access search data. */
public interface SearchDataRepository {

    /**
     * Performs a count using the provided filter.
     *
     * @param conn the {@link SqlConnection}
     * @param filter the {@link Filter} to be used to build the query condition.
     * @return the count as a {@link Uni<Long>}
     */
    Uni<Long> count(SqlConnection conn, Filter filter);

    /**
     * Performs a search using the provided filter and pagination params.
     *
     * @param conn the {@link SqlConnection}
     * @param filter the {@link Filter} to be used to build the query condition.
     * @return the result of the search as a {@link List<SearchDataDto>} in a {@link Uni<>}
     */
    Uni<List<SearchDataDto>> search(
            SqlConnection conn, Filter filter, Integer offset, Integer limit);
}
