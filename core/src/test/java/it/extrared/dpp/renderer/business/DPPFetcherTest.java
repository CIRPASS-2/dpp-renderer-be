package it.extrared.dpp.renderer.business;

import static it.extrared.dpp.renderer.utils.CommonUtils.JSON_LD_MIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import io.quarkus.runtime.util.StringUtil;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.RunOnVertxContext;
import io.quarkus.test.vertx.UniAsserter;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class DPPFetcherTest {

    @Inject DPPFetcher fetcher;

    @Test
    @RunOnVertxContext
    public void testFetchForComparison(UniAsserter asserter) {
        Uni<Model> result = fetcher.fetchDPPForComparison("http://smartphone/json-ld");
        asserter.assertThat(
                () -> result,
                m -> {
                    assertEquals(2, m.listNameSpaces().toList().size());
                });
    }

    @Test
    @RunOnVertxContext
    public void testFetchForComparison2(UniAsserter asserter) {
        Uni<Model> result = fetcher.fetchDPPForComparison("http://laptop/rdf-xml");
        asserter.assertThat(
                () -> result,
                m -> {
                    assertEquals(2, m.listNameSpaces().toList().size());
                });
    }

    @Test
    @RunOnVertxContext
    public void testFetchAsString(UniAsserter asserter) {
        Uni<DPPWithContentType> result = fetcher.fetchDPPAsString("http://smartphone/json-ld");
        asserter.assertThat(
                () -> result,
                r -> {
                    assertEquals(JSON_LD_MIME, r.contentType());
                    assertFalse(StringUtil.isNullOrEmpty(r.dpp()));
                });
    }

    @Test
    @RunOnVertxContext
    public void testFetchAsString2(UniAsserter asserter) {
        Uni<DPPWithContentType> result = fetcher.fetchDPPAsString("http://laptop/rdf-xml");
        asserter.assertThat(
                () -> result,
                r -> {
                    assertEquals(JSON_LD_MIME, r.contentType());
                    assertFalse(StringUtil.isNullOrEmpty(r.dpp()));
                });
    }

    @Test
    @RunOnVertxContext
    public void testFetchAsString3(UniAsserter asserter) {
        Uni<DPPWithContentType> result = fetcher.fetchDPPAsString("http://hoven/plain-json");
        asserter.assertThat(
                () -> result,
                r -> {
                    assertEquals(MediaType.APPLICATION_JSON, r.contentType());
                    assertFalse(StringUtil.isNullOrEmpty(r.dpp()));
                });
    }
}
