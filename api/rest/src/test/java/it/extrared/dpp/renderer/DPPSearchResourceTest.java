package it.extrared.dpp.renderer;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import it.extrared.dpp.renderer.business.search.dto.FilterDto;
import it.extrared.dpp.renderer.business.search.dto.FilterOp;
import it.extrared.dpp.renderer.business.search.dto.SearchDto;
import it.extrared.dpp.renderer.dto.PageResult;
import it.extrared.dpp.renderer.dto.SearchDataDto;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class DPPSearchResourceTest {

    @Test
    public void testSearchAll() {
        PageResult<SearchDataDto> result =
                given().when()
                        .contentType(ContentType.JSON)
                        .post("/search/v1")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(new TypeRef<PageResult<SearchDataDto>>() {});
        assertEquals(5, result.getCount());
        assertEquals(result.getCount(), result.getElements().size());
    }

    @Test
    public void testSearch() {
        SearchDto searchDto = new SearchDto();
        FilterDto bool = new FilterDto();
        bool.setLiteral("false");
        bool.setOp(FilterOp.EQ);
        bool.setProperty("boolField");

        FilterDto intF = new FilterDto();
        intF.setProperty("intField");
        intF.setOp(FilterOp.LT);
        intF.setLiteral("23");

        FilterDto doubleF = new FilterDto();
        doubleF.setProperty("doubleField");
        doubleF.setOp(FilterOp.GTE);
        doubleF.setLiteral("0.4");
        searchDto.setFilters(List.of(bool, intF, doubleF));
        searchDto.setLimit(5);
        searchDto.setOffset(0);
        PageResult<SearchDataDto> result =
                given().when()
                        .contentType(ContentType.JSON)
                        .body(searchDto)
                        .post("/search/v1")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(new TypeRef<PageResult<SearchDataDto>>() {});
        assertEquals(2, result.getCount());
        assertEquals(result.getCount(), result.getElements().size());
        for (SearchDataDto s : result.getElements()) {
            assertFalse(s.getData().get("boolField").asBoolean());
            assertTrue(s.getData().get("intField").asInt() < 23);
            assertTrue(s.getData().get("doubleField").asDouble() >= 0.4);
        }
    }

    @Test
    public void testSearch2() {
        SearchDto searchDto = new SearchDto();
        FilterDto bool = new FilterDto();
        bool.setLiteral("true");
        bool.setOp(FilterOp.EQ);
        bool.setProperty("boolField");

        FilterDto intF = new FilterDto();
        intF.setProperty("intField");
        intF.setOp(FilterOp.GT);
        intF.setLiteral("10");

        FilterDto doubleF = new FilterDto();
        doubleF.setProperty("doubleField");
        doubleF.setOp(FilterOp.LTE);
        doubleF.setLiteral("49.3");
        searchDto.setFilters(List.of(bool, intF, doubleF));
        searchDto.setLimit(5);
        searchDto.setOffset(0);
        PageResult<SearchDataDto> result =
                given().when()
                        .contentType(ContentType.JSON)
                        .body(searchDto)
                        .post("/search/v1")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(new TypeRef<PageResult<SearchDataDto>>() {});
        assertEquals(2, result.getCount());
        assertEquals(result.getCount(), result.getElements().size());
        for (SearchDataDto s : result.getElements()) {
            assertTrue(s.getData().get("boolField").asBoolean());
            assertTrue(s.getData().get("intField").asInt() > 10);
            assertTrue(s.getData().get("doubleField").asDouble() <= 49.3);
        }
    }

    @Test
    public void testSearch3() {
        SearchDto searchDto = new SearchDto();
        FilterDto bool = new FilterDto();
        bool.setLiteral("true");
        bool.setOp(FilterOp.EQ);
        bool.setProperty("boolField");

        FilterDto like = new FilterDto();
        like.setProperty("strField");
        like.setOp(FilterOp.LIKE);
        like.setLiteral("'text'");

        searchDto.setFilters(List.of(bool, like));
        searchDto.setLimit(5);
        searchDto.setOffset(0);
        PageResult<SearchDataDto> result =
                given().when()
                        .contentType(ContentType.JSON)
                        .body(searchDto)
                        .post("/search/v1")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(new TypeRef<PageResult<SearchDataDto>>() {});
        assertEquals(3, result.getCount());
        assertEquals(result.getCount(), result.getElements().size());
        for (SearchDataDto s : result.getElements()) {
            assertTrue(s.getData().get("boolField").asBoolean());
            assertTrue(s.getData().get("strField").asText().startsWith("text"));
        }
    }
}
