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
import java.util.Map;

/** Data class representing the result of a comparison. */
public class ComparisonResult {

    private List<Map<String, Object>> results;

    private ComparisonResult() {}

    /**
     * @return the results as a List of map where each map represent a single DPP extracted values.
     */
    public List<Map<String, Object>> getResults() {
        return results;
    }

    /**
     * Sets the results Map.
     *
     * @param results the results Map.
     */
    public void setResults(List<Map<String, Object>> results) {
        this.results = results;
    }

    /**
     * Creates a builder for an instance this data class.
     *
     * @return the builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ComparisonResult result;

        public Builder() {
            this.result = new ComparisonResult();
        }

        public Builder withResults(List<Map<String, Object>> results) {
            this.result.results = results;
            return this;
        }

        public ComparisonResult build() {
            return result;
        }
    }
}
