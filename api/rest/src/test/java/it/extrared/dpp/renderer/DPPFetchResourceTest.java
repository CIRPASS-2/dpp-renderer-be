package it.extrared.dpp.renderer;

import static io.restassured.RestAssured.given;
import static it.extrared.dpp.renderer.utils.CommonUtils.JSON_LD_MIME;
import static org.junit.jupiter.api.Assertions.assertFalse;

import io.netty.util.internal.StringUtil;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class DPPFetchResourceTest {

    @Test
    public void testFetchPlainJson() {
        String dpp =
                given().param("url", "http://localhost:8080/dpp/plain-json")
                        .get("/fetch/v1")
                        .then()
                        .statusCode(200)
                        .header("Content-Type", MediaType.APPLICATION_JSON)
                        .extract()
                        .body()
                        .asString();
        assertFalse(StringUtil.isNullOrEmpty(dpp));
    }

    @Test
    public void testFetchJsonLd() {
        String dpp =
                given().param("url", "http://localhost:8080/dpp/json-ld")
                        .get("/fetch/v1")
                        .then()
                        .statusCode(200)
                        .header("Content-Type", JSON_LD_MIME)
                        .extract()
                        .body()
                        .asString();
        assertFalse(StringUtil.isNullOrEmpty(dpp));
    }

    @Test
    public void testFetchPlainRdfXml() {
        String dpp =
                given().param("url", "http://localhost:8080/dpp/rdf-xml")
                        .get("/fetch/v1")
                        .then()
                        .statusCode(200)
                        .header("Content-Type", JSON_LD_MIME)
                        .extract()
                        .body()
                        .asString();
        assertFalse(StringUtil.isNullOrEmpty(dpp));
    }
}
