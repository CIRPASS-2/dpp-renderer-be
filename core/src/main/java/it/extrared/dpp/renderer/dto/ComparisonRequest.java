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

import it.extrared.dpp.renderer.business.comparison.OntologyPath;
import java.util.List;
import java.util.Map;

/**
 * Data class with parameters to be used for extract data for comparison purpose from multiple dpp.
 */
public class ComparisonRequest {

    private List<String> dppUrls;

    private Map<String, List<OntologyPath>> propertyPaths;

    public ComparisonRequest(List<String> dppUrls, Map<String, List<OntologyPath>> propertyPaths) {
        this.dppUrls = dppUrls;
        this.propertyPaths = propertyPaths;
    }

    public ComparisonRequest() {}

    /**
     * @return the URLs of the DPPs to retrieve.
     */
    public List<String> getDppUrls() {
        return dppUrls;
    }

    /**
     * Set the list of DPPs URLs.
     *
     * @param dppUrls the list of urls.
     */
    public void setDppUrls(List<String> dppUrls) {
        this.dppUrls = dppUrls;
    }

    /**
     * Get the property paths to the values to be extracted from the DPP for comparison purpose.
     *
     * @return the property paths as a Map where the key is the logical name of the property and the
     *     value is the list of path as an {@link OntologyPath}.
     */
    public Map<String, List<OntologyPath>> getPropertyPaths() {
        return propertyPaths;
    }

    /**
     * Sets the property paths.
     *
     * @param propertyPaths the property paths list.
     */
    public void setPropertyPaths(Map<String, List<OntologyPath>> propertyPaths) {
        this.propertyPaths = propertyPaths;
    }
}
