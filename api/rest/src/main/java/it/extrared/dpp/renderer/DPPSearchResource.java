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
import it.extrared.dpp.renderer.business.search.dto.SearchDto;
import it.extrared.dpp.renderer.dto.PageResult;
import it.extrared.dpp.renderer.dto.SearchDataDto;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.Operation;

/** Represents REST API endpoints group for DPP data search operations. */
@Path("/search/v1")
public interface DPPSearchResource {

    /**
     * Retrieve a list of DPP data from those present in the local DPP data repository.
     *
     * @param searchDto the dto with the search parameters.
     * @return the List of DPP local data matching the input criteria as a {@link
     *     PageResult<SearchDataDto>}.
     */
    @Operation(
            summary = "Searches for DPP data in the local storage.",
            description =
                    """
                    Searches for DPP data in the local storage. It uses the parameters in the searchDto to query the local repository.
                    """)
    @POST
    Uni<PageResult<SearchDataDto>> search(SearchDto searchDto);
}
