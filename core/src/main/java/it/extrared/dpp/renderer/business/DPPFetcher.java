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

import static it.extrared.dpp.renderer.utils.CommonUtils.*;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.document.JsonDocument;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import it.extrared.dpp.renderer.business.comparison.rdf.RDFTypes;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonStructure;
import jakarta.ws.rs.core.MediaType;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.jboss.logging.Logger;

/** This class contains methods to fetch a DPP from the decentralized repository. */
@ApplicationScoped
public class DPPFetcher {

    @Inject WebClient webClient;

    private static final Logger LOGGER = Logger.getLogger(DPPFetcher.class);

    /**
     * Fetch a DPP to be used for a comparison.
     *
     * @param url the url at which the DPP is available
     * @return the DPP as Jena {@link Model}
     */
    public Uni<Model> fetchDPPForComparison(String url) {
        return fetchDPP(url)
                .flatMap(
                        r ->
                                Uni.createFrom()
                                        .item(
                                                () -> {
                                                    Buffer b = r.bodyAsBuffer();
                                                    if (b == null) b = Buffer.buffer();
                                                    String dpp = new String(b.getBytes());
                                                    String cType = r.getHeader(CONTENT_TYPE);
                                                    if (isJsonContentType(cType)
                                                            && !isJsonLd(dpp)) {
                                                        throw new RuntimeException(
                                                                "Unable to use plain json DPP for comparison.");
                                                    }
                                                    RDFTypes.RDFType rdf =
                                                            RDFTypes.fromContentType(cType);
                                                    return rdf.read(new StringReader(dpp));
                                                })
                                        .runSubscriptionOn(Infrastructure.getDefaultWorkerPool()));
    }

    /**
     * Retrieve a DPP from the decentralized repository.
     *
     * @param url the url at which the DPP is available.
     * @return a {@link DPPWithContentType} containing the DPP as a String and the content type (can
     *     be either application/json or application/ld+json).
     */
    public Uni<DPPWithContentType> fetchDPPAsString(String url) {
        return fetchDPP(url)
                .flatMap(
                        r ->
                                Uni.createFrom()
                                        .item(
                                                () ->
                                                        mapWithContentType(
                                                                r.bodyAsBuffer(),
                                                                r.getHeader(CONTENT_TYPE)))
                                        .runSubscriptionOn(Infrastructure.getDefaultWorkerPool()));
    }

    private DPPWithContentType mapWithContentType(Buffer buffer, String contentType) {
        String normalized = normalizeContentType(contentType);
        debug(() -> "Normalized content type is %s".formatted(normalized), LOGGER);
        if (buffer == null) {
            debug(() -> "Null DPP received", LOGGER);
            return new DPPWithContentType(null, normalized);
        }
        byte[] b = buffer.getBytes();
        String dpp = new String(b);
        if (isJsonContentType(normalized)) {
            if (isJsonLd(dpp)) {
                debug(() -> "JSON-LD dpp served with json mime type %s".formatted(dpp), LOGGER);
                return new DPPWithContentType(expandJsonLd(dpp), JSON_LD_MIME);
            } else {
                debug(() -> "JSON dpp %s".formatted(dpp), LOGGER);
                return new DPPWithContentType(dpp, MediaType.APPLICATION_JSON);
            }
        } else {
            debug(() -> "RDF DPP detected. Applying conversion to JSON-LD if neede...", LOGGER);
            RDFTypes.RDFType rdf = RDFTypes.fromContentType(normalized);
            return new DPPWithContentType(
                    convertToJsonLd(rdf.read(new StringReader(dpp))), JSON_LD_MIME);
        }
    }

    private Uni<HttpResponse<Buffer>> fetchDPP(String url) {
        debug(() -> "Fetching DPP from url %s".formatted(url), LOGGER);
        List<String> mimes = RDFTypes.getSupportedContentTypes();
        HttpRequest<Buffer> request = webClient.getAbs(url);
        request.headers().add("Accept", String.join(", ", mimes));
        return request.send();
    }

    private String convertToJsonLd(Model model) {
        debug(() -> "Converting DPP to a JSON-LD", LOGGER);
        StringWriter compactWriter = new StringWriter();
        RDFDataMgr.write(compactWriter, model, Lang.JSONLD);
        String dpp = compactWriter.toString();
        debug(() -> "DPP converted: %s".formatted(dpp), LOGGER);
        return expandJsonLd(dpp);
    }

    private String expandJsonLd(String payload) {
        try {
            debug(() -> "Expanding dpp %s".formatted(payload), LOGGER);
            JsonDocument doc = JsonDocument.of(new StringReader(payload));
            JsonStructure expanded = JsonLd.expand(doc).get();
            String result = expanded.toString();
            debug(() -> "DPP expanded %s".formatted(result), LOGGER);
            return result;
        } catch (Exception e) {
            error(() -> "Error expanding DPP:\n", e, LOGGER);
            throw new RuntimeException("Failed to expand JSON-LD", e);
        }
    }
}
