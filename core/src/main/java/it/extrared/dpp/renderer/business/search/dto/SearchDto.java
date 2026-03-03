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

import it.extrared.dpp.renderer.business.search.filter.All;
import it.extrared.dpp.renderer.business.search.filter.And;
import it.extrared.dpp.renderer.business.search.filter.Filter;
import java.util.List;

/** Dto to perform a search operations. */
public class SearchDto {

    private List<FilterDto> filters;

    private Integer offset;

    private Integer limit;

    public SearchDto() {}

    /**
     * @return the filters to search by.
     */
    public List<FilterDto> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterDto> filters) {
        this.filters = filters;
    }

    /**
     * The 0 based index from which starts the retrieval of the values.
     *
     * @return the index.
     */
    public Integer getOffset() {
        if (offset == null) offset = 0;
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /**
     * @return the number of result to return.
     */
    public Integer getLimit() {
        if (limit == null) limit = 20;
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * Get the filter representing this search operation. If the filter in the dto list are 0 an
     * {@link All} filter is provied. If the filter is one, that filter is returned. If filters are
     * multiple then a single {@link And} filter is returned.
     *
     * @return
     */
    public Filter getFilter() {
        if (filters == null || filters.isEmpty()) return new All();
        if (filters.size() == 1) return filters.getFirst().readFilter();
        else return new And(filters.stream().map(FilterDto::readFilter).toArray(Filter[]::new));
    }

    /**
     * @return an empty instance of the {@link SearchDto}.
     */
    public static SearchDto empty() {
        SearchDto dto = new SearchDto();
        dto.setLimit(20);
        dto.setOffset(0);
        return dto;
    }
}
