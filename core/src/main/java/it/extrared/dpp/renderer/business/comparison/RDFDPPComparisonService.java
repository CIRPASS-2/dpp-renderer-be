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
package it.extrared.dpp.renderer.business.comparison;

import static it.extrared.dpp.renderer.utils.CommonUtils.debug;
import static it.extrared.dpp.renderer.utils.CommonUtils.sanitizeVarNameForSparQL;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.groups.UniAndGroupIterable;
import it.extrared.dpp.renderer.business.DPPFetcher;
import it.extrared.dpp.renderer.business.comparison.rdf.MultiFieldSparQLConverter;
import it.extrared.dpp.renderer.business.comparison.rdf.SPARQLExecutor;
import it.extrared.dpp.renderer.dto.ComparisonRequest;
import it.extrared.dpp.renderer.dto.ComparisonResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import org.apache.jena.rdf.model.Model;
import org.jboss.logging.Logger;

/**
 * Implementation of a {@link DPPComparisonService} able to compare (i.e. extract same set of
 * values) out of RDF based DPPs. It expects DPP properties provided with format
 * path.to[*@type=TheType].property convert them in a SPARQL queries and return the extracted values
 * from the various DPP.
 */
@ApplicationScoped
public class RDFDPPComparisonService implements DPPComparisonService {
    @Inject DPPFetcher dppFetcher;

    @Inject SPARQLExecutor executor;

    private static final Logger LOGGER = Logger.getLogger(RDFDPPComparisonService.class);

    @Override
    public Uni<ComparisonResult> compareDPPs(ComparisonRequest request) {
        MultiFieldSparQLConverter converter =
                new MultiFieldSparQLConverter(request.getPropertyPaths());
        String sparql = converter.convert();
        debug(() -> "Obtained sparql query is %s".formatted(sparql), LOGGER);
        List<Uni<Map<String, Object>>> unis =
                request.getDppUrls().stream()
                        .map(u -> execSingle(u, request.getPropertyPaths(), sparql))
                        .toList();
        UniAndGroupIterable<Map<String, Object>> all = Uni.combine().all().unis(unis);
        return all.with(
                l -> ComparisonResult.builder().withResults((List<Map<String, Object>>) l).build());
    }

    private Uni<Map<String, Object>> execSingle(
            String url, Map<String, List<OntologyPath>> paths, String sparql) {
        debug(() -> "Fetching dpp for comparison from %s".formatted(url), LOGGER);
        Uni<Model> dpp = dppFetcher.fetchDPPForComparison(url);
        return dpp.map(m -> executeSingle(m, paths, sparql));
    }

    private Map<String, Object> executeSingle(
            Model model, Map<String, List<OntologyPath>> fields, String sparql) {
        debug(() -> "Executing sparql to extract comparison fields", LOGGER);
        Map<String, Object> result = executor.executeQuery(sparql, model);
        for (String fieldName : fields.keySet()) {
            debug(() -> "Adding field with name %s to results.".formatted(fieldName), LOGGER);
            result.putIfAbsent(sanitizeVarNameForSparQL(fieldName), null);
        }
        return result;
    }
}
