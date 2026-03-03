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
package it.extrared.dpp.renderer.business;

import static it.extrared.dpp.renderer.utils.CommonUtils.debug;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import it.extrared.dpp.renderer.business.search.filter.All;
import it.extrared.dpp.renderer.business.search.filter.Filter;
import it.extrared.dpp.renderer.dto.PageResult;
import it.extrared.dpp.renderer.dto.SearchDataDto;
import it.extrared.dpp.renderer.repository.SearchDataRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import org.jboss.logging.Logger;

/** Service class to search DPP data in a {@link SearchDataRepository} */
@ApplicationScoped
public class SearchService {

    @Inject SearchDataRepository repository;
    @Inject Pool pool;

    private static final Logger LOGGER = Logger.getLogger(SearchService.class);

    /**
     * Search data according to the input filter. It returns records according to the input
     * pagination parameters.
     *
     * @param filter the filter to build the query condition.
     * @param offset the offset from which return the records.
     * @param limit how many records should be returned.
     * @return a {@link PageResult<SearchDataDto>} containing the list of search data retrieved +
     *     the total number of element matched by the filter.
     */
    public Uni<PageResult<SearchDataDto>> search(Filter filter, Integer offset, Integer limit) {
        debug(() -> "Search for dpp data...", LOGGER);
        Filter actual = filter == null ? new All() : filter;
        return pool.withConnection(
                c ->
                        repository
                                .count(c, actual)
                                .flatMap(
                                        count ->
                                                repository
                                                        .search(c, actual, offset, limit)
                                                        .map(l -> asPageResult(count, l))));
    }

    private PageResult<SearchDataDto> asPageResult(Long count, List<SearchDataDto> results) {
        debug(() -> "Count %s and result size is %s".formatted(count, results.size()), LOGGER);
        PageResult.Builder<SearchDataDto> builder = PageResult.builder();
        return builder.withCount(count)
                .withElements(results)
                .withNumberOfElements(results.size())
                .build();
    }
}
