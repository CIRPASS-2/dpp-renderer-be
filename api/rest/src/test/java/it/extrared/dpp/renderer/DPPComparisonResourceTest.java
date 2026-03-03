package it.extrared.dpp.renderer;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import it.extrared.dpp.renderer.business.comparison.OntologyPath;
import it.extrared.dpp.renderer.dto.ComparisonRequest;
import it.extrared.dpp.renderer.dto.ComparisonResult;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class DPPComparisonResourceTest {

    @Test
    public void testComparison() {
        ComparisonRequest request = new ComparisonRequest();
        Map<String, List<OntologyPath>> paths =
                Map.of(
                        "productName",
                                List.of(
                                        new OntologyPath(
                                                "http://dpp.taltech.ee/MODDPP#", "itemName"),
                                        new OntologyPath(
                                                "http://dpp.taltech.ee/EUDPP#", "productName")),
                        "carbonFootprint",
                                List.of(
                                        new OntologyPath(
                                                "http://dpp.taltech.ee/MODDPP#",
                                                "hasAttribute[*@type=GHGFootprint].quantity"),
                                        new OntologyPath(
                                                "http://dpp.taltech.ee/EUDPP#",
                                                "hasProperty[*@type=CarbonFootprint].numericalValue")),
                        "energyConsumption",
                                List.of(
                                        new OntologyPath(
                                                "http://dpp.taltech.ee/MODDPP#",
                                                "hasAttribute[*@type=PowerDraw].quantity"),
                                        new OntologyPath(
                                                "http://dpp.taltech.ee/EUDPP#",
                                                "hasProperty[*@type=EnergyConsumption].numericalValue")),
                        "recyclingRate",
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
        ComparisonResult comparisonResult =
                given().when()
                        .contentType(ContentType.JSON)
                        .body(request)
                        .post("/comparison/v1")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(ComparisonResult.class);

        assertEquals(6, comparisonResult.getResults().size());
    }
}
