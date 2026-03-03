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
package it.extrared.dpp.renderer;

import io.smallrye.mutiny.Uni;
import it.extrared.dpp.renderer.dto.ComparisonRequest;
import it.extrared.dpp.renderer.dto.ComparisonResult;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.reactive.RestResponse;

/** Represents REST API endpoints group for DPP comparison related operations. */
@Path("/comparison/v1")
public interface DPPComparisonResource {

    /**
     * Issue a comparison request.
     * @param comparisonRequest the dto with the comparison request endpoints, logical field names and paths.
     * @return the fields extracted from the DPPs retrieved from the provided endpoints as a {@link ComparisonResult}.
     */
    @Operation(
            summary = "Perform a comparison operation.",
            description =
                    """
                    Perform a comparison operation between two or more DPPs. The request body must provide the list of URIs at which the DPPs are available plus the
                    list of properties to be extracted. Each property extraction configuration consist of a logical name (i.e. the name that the corresponding value will have in the results) plus a list of property paths with a vocabulary ns to test on a DPP to extract that value.
                    Properties must be provided with format path.to[*@type=ToType].property. The endpoint supports currently only
                    comparison between RDF compatible DPP. Plain JSON DPP comparison is not supported.
                    """)
    @POST
    Uni<RestResponse<ComparisonResult>> compare(ComparisonRequest comparisonRequest);
}
