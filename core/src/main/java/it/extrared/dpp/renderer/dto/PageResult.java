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
package it.extrared.dpp.renderer.dto;

import java.util.List;

/**
 * Represent a result with pagination information.
 *
 * @param <T> the type of the entity paginated.
 */
public class PageResult<T> {

    private List<T> elements;

    private Long count;

    private Integer numberOfElements;

    public List<T> getElements() {
        return elements;
    }

    public Long getCount() {
        return count;
    }

    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private final PageResult<T> result;

        private Builder() {
            this.result = new PageResult<>();
        }

        public Builder<T> withElements(List<T> elements) {
            result.elements = elements;
            return this;
        }

        public Builder<T> withCount(Long count) {
            result.count = count;
            return this;
        }

        public Builder<T> withNumberOfElements(Integer numberOfElements) {
            result.numberOfElements = numberOfElements;
            return this;
        }

        public PageResult<T> build() {
            return result;
        }
    }
}
