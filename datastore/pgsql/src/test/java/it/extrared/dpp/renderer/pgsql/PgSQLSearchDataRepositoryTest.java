package it.extrared.dpp.renderer.pgsql;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.RunOnVertxContext;
import io.quarkus.test.vertx.UniAsserter;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import it.extrared.dpp.renderer.business.search.filter.*;
import it.extrared.dpp.renderer.dto.SearchDataDto;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class PgSQLSearchDataRepositoryTest {

    @Inject Pool pool;

    @Inject PgSQLSearchDataRepository repository;

    @Test
    @RunOnVertxContext
    public void testCountAll(UniAsserter asserter) {
        Uni<Long> count = pool.withConnection(c -> repository.count(c, new All()));
        asserter.assertEquals(() -> count, 5L);
    }

    @Test
    @RunOnVertxContext
    public void testCountFiltered(UniAsserter asserter) {
        Equals eq = new Equals(new Property("boolField"), new Literal("false"));
        LowerThan gt = new LowerThan(new Property("intField"), new Literal(23));
        GreaterThanEq lte = new GreaterThanEq(new Property("doubleField"), new Literal(0.4));
        Uni<Long> count = pool.withConnection(c -> repository.count(c, new And(eq, gt, lte)));
        asserter.assertEquals(() -> count, 2L);
    }

    @Test
    @RunOnVertxContext
    public void testCountFiltered2(UniAsserter asserter) {
        Equals eq = new Equals(new Property("boolField"), new Literal("true"));
        GreaterThan gt = new GreaterThan(new Property("intField"), new Literal(10));
        LowerThanEq lte = new LowerThanEq(new Property("doubleField"), new Literal(49.3));
        Uni<Long> count = pool.withConnection(c -> repository.count(c, new And(eq, gt, lte)));
        asserter.assertEquals(() -> count, 2L);
    }

    @Test
    @RunOnVertxContext
    public void testCountFiltered3(UniAsserter asserter) {
        Equals eq = new Equals(new Property("boolField"), new Literal("true"));
        Like like = new Like(new Property("strField"), new Literal("'text'"));
        Uni<Long> count = pool.withConnection(c -> repository.count(c, new And(eq, like)));
        asserter.assertEquals(() -> count, 3L);
    }

    @Test
    @RunOnVertxContext
    public void testSearchAll(UniAsserter asserter) {
        Uni<List<SearchDataDto>> result =
                pool.withConnection(c -> repository.search(c, new All(), 0, 5));
        asserter.assertEquals(() -> result.map(List::size), 5);
    }

    @Test
    @RunOnVertxContext
    public void testSearchFiltered(UniAsserter asserter) {
        Equals eq = new Equals(new Property("boolField"), new Literal("false"));
        LowerThan gt = new LowerThan(new Property("intField"), new Literal(23));
        GreaterThanEq lte = new GreaterThanEq(new Property("doubleField"), new Literal(0.4));
        Uni<List<SearchDataDto>> result =
                pool.withConnection(c -> repository.search(c, new And(eq, gt, lte), 0, 5));
        asserter.assertThat(
                () -> result,
                l -> {
                    for (SearchDataDto s : l) {
                        assertFalse(s.getData().get("boolField").asBoolean());
                        assertTrue(s.getData().get("intField").asInt() < 23);
                        assertTrue(s.getData().get("doubleField").asDouble() >= 0.4);
                    }
                });
    }

    @Test
    @RunOnVertxContext
    public void testSearchFiltered2(UniAsserter asserter) {
        Equals eq = new Equals(new Property("boolField"), new Literal("true"));
        GreaterThan gt = new GreaterThan(new Property("intField"), new Literal(10));
        LowerThanEq lte = new LowerThanEq(new Property("doubleField"), new Literal(49.3));
        Uni<List<SearchDataDto>> result =
                pool.withConnection(c -> repository.search(c, new And(eq, gt, lte), 0, 5));
        asserter.assertThat(
                () -> result,
                l -> {
                    for (SearchDataDto s : l) {
                        assertTrue(s.getData().get("boolField").asBoolean());
                        assertTrue(s.getData().get("intField").asInt() > 10);
                        assertTrue(s.getData().get("doubleField").asDouble() <= 49.3);
                    }
                });
    }

    @Test
    @RunOnVertxContext
    public void testSearchFiltered3(UniAsserter asserter) {
        Equals eq = new Equals(new Property("boolField"), new Literal("true"));
        Like like = new Like(new Property("strField"), new Literal("'text'"));
        Uni<List<SearchDataDto>> result =
                pool.withConnection(c -> repository.search(c, new And(eq, like), 0, 5));
        asserter.assertThat(
                () -> result,
                l -> {
                    for (SearchDataDto s : l) {
                        assertTrue(s.getData().get("boolField").asBoolean());
                        assertTrue(s.getData().get("strField").asText().startsWith("text"));
                    }
                });
    }
}
