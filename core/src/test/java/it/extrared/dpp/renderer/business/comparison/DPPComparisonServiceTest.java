package it.extrared.dpp.renderer.business.comparison;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.RunOnVertxContext;
import io.quarkus.test.vertx.UniAsserter;
import io.smallrye.mutiny.Uni;
import it.extrared.dpp.renderer.dto.ComparisonRequest;
import it.extrared.dpp.renderer.dto.ComparisonResult;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class DPPComparisonServiceTest {

    @Inject DPPComparisonService comparisonService;

    @Test
    @RunOnVertxContext
    public void testComparison(UniAsserter asserter) {
        ComparisonRequest request = new ComparisonRequest();
        Map<String, List<OntologyPath>> paths =
                Map.of(
                        "productName",
                                List.of(
                                        new OntologyPath(
                                                "http://dpp.taltech.ee/MODDPP#", "itemName"),
                                        new OntologyPath(
                                                "http://dpp.taltech.ee/EUDPP#", "productName")),
                        "carbon Footprint",
                                List.of(
                                        new OntologyPath(
                                                "http://dpp.taltech.ee/MODDPP#",
                                                "hasAttribute[*@type=GHGFootprint].quantity"),
                                        new OntologyPath(
                                                "http://dpp.taltech.ee/EUDPP#",
                                                "hasProperty[*@type=CarbonFootprint].numericalValue")),
                        "energy Consumption",
                                List.of(
                                        new OntologyPath(
                                                "http://dpp.taltech.ee/MODDPP#",
                                                "hasAttribute[*@type=PowerDraw].quantity"),
                                        new OntologyPath(
                                                "http://dpp.taltech.ee/EUDPP#",
                                                "hasProperty[*@type=EnergyConsumption].numericalValue")),
                        "recycling Rate",
                                List.of(
                                        new OntologyPath(
                                                "http://dpp.taltech.ee/MODDPP#",
                                                "hasAttribute[*@type=ReuseRate].quantity"),
                                        new OntologyPath(
                                                "http://dpp.taltech.ee/EUDPP#",
                                                "hasProperty[*@type=RecyclingRate].numericalValue")));
        request.setPropertyPaths(paths);

        request.setDppUrls(
                List.of(
                        "http://smartphone/json-ld",
                        "http://smartphone/json-mod-ld",
                        "http://laptop/rdf-mod-xml",
                        "http://battery/rdf-ttl",
                        "http://shoes/rdf-nt",
                        "http://fridge/rdf-n3"));
        Uni<ComparisonResult> resultUni = comparisonService.compareDPPs(request);
        asserter.assertThat(
                () -> resultUni,
                res -> {
                    List<Map<String, Object>> results = res.getResults();
                    assertEquals(6, results.size());
                });
    }
}
