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
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;

/** Represents REST API endpoints group to fetch a DPP from the decentralized repository. */
@Path("/fetch/v1")
public interface DPPFetchResource {

    /**
     * Retrieve a DPP from the decentralized repository and returns it either as a JSON or as an
     * expanded JSON-LD
     *
     * @param url the url at which the DPP is available.
     * @return the DPP as a JSON/JSON-LD string.
     */
    @Operation(
            summary = "Fetches a DPP from the remote repository",
            description =
                    """
                    Fetches a DPP from the remote repository. If the DPP is retrieved in a RDF compatible format it is returned as an
                    expanded JSON-LD. If the DPP is retrieved as a plain JSON it is returned as it is.
                    """)
    @Parameter(
            name = "url",
            description =
                    """
                    The url at which the DPP is available in the remote repository.
                    """,
            in = ParameterIn.PATH)
    @GET
    @Produces({"application/ld+json", MediaType.APPLICATION_JSON})
    Uni<RestResponse<String>> fetchDPP(@RestQuery("url") String url);
}
