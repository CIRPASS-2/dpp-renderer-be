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
package it.extrared.dpp.renderer.business.comparison.rdf;

import static it.extrared.dpp.renderer.utils.CommonUtils.debug;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.jboss.logging.Logger;

/** Class to execute SPARQL queries */
@ApplicationScoped
public class SPARQLExecutor {

    private static final Logger LOGGER = Logger.getLogger(SPARQLExecutor.class);

    /**
     * Execute a SPARQL query over a Jena model and return the result of the selection.
     *
     * @param sparql the SPARQL query.
     * @param model the jena {@link Model}
     * @return the query result as a Map.
     */
    public Map<String, Object> executeQuery(String sparql, Model model) {
        debug(() -> "Executing sparql %s".formatted(sparql), LOGGER);
        try (QueryExecution qexec =
                QueryExecutionFactory.create(QueryFactory.create(sparql), model)) {

            ResultSet rs = qexec.execSelect();
            Map<String, Object> values = new HashMap<>();

            while (rs.hasNext()) {
                debug(() -> "Query has solutions!", LOGGER);
                QuerySolution sol = rs.next();
                Iterator<String> vNames = sol.varNames();
                while (vNames.hasNext()) {
                    String var = vNames.next();
                    RDFNode rdfNode = sol.get(var);
                    if (rdfNode != null) {
                        debug(() -> "Adding solution %s".formatted(var), LOGGER);
                        values.put(var, extractValue(rdfNode));
                    }
                }
            }
            return values;
        }
    }

    private String extractValue(RDFNode node) {
        if (node.isLiteral()) return node.asLiteral().getLexicalForm();
        if (node.isResource()) return node.asResource().getURI();
        return node.toString();
    }
}
