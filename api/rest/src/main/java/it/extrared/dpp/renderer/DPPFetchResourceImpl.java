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
import it.extrared.dpp.renderer.business.DPPFetcher;
import it.extrared.dpp.renderer.business.DPPWithContentType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.resteasy.reactive.RestResponse;

/** Implementation of the endpoints described in {@link DPPFetchResource}. */
@ApplicationScoped
public class DPPFetchResourceImpl implements DPPFetchResource {

    @Inject DPPFetcher fetcher;

    @Override
    public Uni<RestResponse<String>> fetchDPP(String url) {
        return fetcher.fetchDPPAsString(url).map(this::asResponse);
    }

    private RestResponse<String> asResponse(DPPWithContentType dppWithContentType) {
        RestResponse.ResponseBuilder<String> builder = RestResponse.ResponseBuilder.create(200);
        return builder.type(dppWithContentType.contentType())
                .entity(dppWithContentType.dpp())
                .build();
    }
}
