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

/** Filter implementation that concatenate multiple filters in a logical and. */
public class And implements Filter {

    private Filter[] filters;

    public And(Filter... filters) {
        this.filters = filters;
    }

    public Filter[] getFilters() {
        return filters;
    }

    public void setFilters(Filter[] filters) {
        this.filters = filters;
    }

    @Override
    public void accept(FilterVisitor visitor, Map<String, Object> extradata) {
        visitor.visit(this, extradata);
    }
}
