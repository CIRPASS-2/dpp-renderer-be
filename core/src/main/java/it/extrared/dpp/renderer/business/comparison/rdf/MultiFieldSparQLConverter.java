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
import static it.extrared.dpp.renderer.utils.CommonUtils.sanitizeVarNameForSparQL;

import it.extrared.dpp.renderer.business.comparison.OntologyPath;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jboss.logging.Logger;

/**
 * This class is able to convert a path in the form
 * propertyName[*@type=SomeType].nestProperty[*@type=NestedType].value to a SparQL query to retrieve
 * the value the path points to in a RDF graph. It handles multi vocabulary SparQL queries i.e. path
 * resolving to select for RDF properties compliant to different ontologies.
 */
public class MultiFieldSparQLConverter {

    private static final Pattern TYPE_FILTER_PATTERN = Pattern.compile("\\[\\*(?:@type=([^]]+))?]");

    private static final Logger LOGGER = Logger.getLogger(MultiFieldSparQLConverter.class);
    private final Map<String, List<OntologyPath>> fields;
    private final Map<String, String> namespacePrefixes = new LinkedHashMap<>();

    public MultiFieldSparQLConverter(Map<String, List<OntologyPath>> fields) {
        this.fields = fields;
        assignNamespacePrefixes();
    }

    private void assignNamespacePrefixes() {
        int idx = 0;
        for (List<OntologyPath> candidates : fields.values()) {
            for (OntologyPath candidate : candidates) {
                String ns = normalizeNamespace(candidate.namespace());
                if (!namespacePrefixes.containsKey(ns)) {
                    namespacePrefixes.put(ns, "ns" + idx++);
                }
            }
        }
    }

    /**
     * Convert the list fields passed in the constructor to a SPARQL query.
     *
     * @return the SPARQL query.
     */
    public String convert() {
        StringBuilder sparql = new StringBuilder();

        namespacePrefixes.forEach(
                (ns, alias) ->
                        sparql.append("PREFIX ")
                                .append(alias)
                                .append(": <")
                                .append(ns)
                                .append(">\n"));

        // use the key of each map entry as the alias for each property path assigned to that key
        sparql.append("SELECT\n");
        for (Map.Entry<String, List<OntologyPath>> entry : fields.entrySet()) {
            String fieldVar = sanitizeVarNameForSparQL(entry.getKey());
            debug(
                    () -> "Converting paths to sparql for logical name %s".formatted(fieldVar),
                    LOGGER);
            List<OntologyPath> candidates = entry.getValue();

            if (candidates.size() == 1) {
                // rename using the alias
                sparql.append("  (?")
                        .append(fieldVar)
                        .append("_0 AS ?")
                        .append(fieldVar)
                        .append(")\n");
            } else {
                // we have more than one candidates so uses coalesce to pick the non null selection
                // result if any.
                sparql.append("  (COALESCE(");
                for (int i = 0; i < candidates.size(); i++) {
                    if (i > 0) sparql.append(", ");
                    sparql.append("?").append(fieldVar).append("_").append(i);
                }
                sparql.append(") AS ?").append(fieldVar).append(")\n");
            }
        }

        sparql.append("WHERE {\n");
        sparql.append("  ?root a ?anyType .\n");

        for (Map.Entry<String, List<OntologyPath>> entry : fields.entrySet()) {
            String fieldVar = sanitizeVarNameForSparQL(entry.getKey());
            List<OntologyPath> candidates = entry.getValue();

            for (int i = 0; i < candidates.size(); i++) {
                OntologyPath candidate = candidates.get(i);
                String ns = normalizeNamespace(candidate.namespace());
                String nsAlias = namespacePrefixes.get(ns);
                String leafVar = "?" + fieldVar + "_" + i;

                sparql.append("  OPTIONAL {\n");
                buildPathPattern(
                        sparql, "?root", candidate.path(), nsAlias, fieldVar + "_" + i, leafVar);
                sparql.append("  }\n");
            }
        }

        sparql.append("}\n");
        return sparql.toString();
    }

    private void buildPathPattern(
            StringBuilder sparql,
            String startVar,
            String path,
            String nsAlias,
            String varPrefix,
            String leafVar) {
        debug(() -> "Converting path %s".formatted(path), LOGGER);
        String[] segments = path.split("\\.");
        String currentVar = startVar;

        for (int i = 0; i < segments.length; i++) {
            String segment = segments[i].trim();
            if (segment.isEmpty()) continue;

            PropertySegment parsed = parseSegment(segment);
            boolean isLast = (i == segments.length - 1);
            String nextVar = isLast ? leafVar : "?" + varPrefix + "_v" + i;

            sparql.append("    ")
                    .append(currentVar)
                    .append(" ")
                    .append(nsAlias)
                    .append(":")
                    .append(parsed.propertyName())
                    .append(" ")
                    .append(nextVar)
                    .append(" .\n");

            if (parsed.typeFilter() != null) {
                sparql.append("    ")
                        .append(nextVar)
                        .append(" a ")
                        .append(nsAlias)
                        .append(":")
                        .append(parsed.typeFilter())
                        .append(" .\n");
            }
            debug(() -> "Sparql now is %s".formatted(sparql), LOGGER);

            currentVar = nextVar;
        }
    }

    private PropertySegment parseSegment(String segment) {
        Matcher matcher = TYPE_FILTER_PATTERN.matcher(segment);
        if (matcher.find()) {
            String propertyName = segment.substring(0, matcher.start());
            String typeFilter = matcher.group(1);
            return new PropertySegment(propertyName, typeFilter);
        }
        return new PropertySegment(segment, null);
    }

    private String normalizeNamespace(String uri) {
        debug(() -> "Normalizing ns %s".formatted(uri), LOGGER);
        if (!uri.endsWith("#") && !uri.endsWith("/")) return uri + "#";
        return uri;
    }

    private record PropertySegment(String propertyName, String typeFilter) {}
}
