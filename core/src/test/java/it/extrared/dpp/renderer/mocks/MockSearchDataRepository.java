package it.extrared.dpp.renderer.mocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.arc.DefaultBean;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlConnection;
import it.extrared.dpp.renderer.business.search.filter.Filter;
import it.extrared.dpp.renderer.dto.SearchDataDto;
import it.extrared.dpp.renderer.repository.SearchDataRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@DefaultBean
@ApplicationScoped
public class MockSearchDataRepository implements SearchDataRepository {

    @Inject ObjectMapper objectMapper;

    @Override
    public Uni<Long> count(SqlConnection conn, Filter filter) {
        return Uni.createFrom().item(3L);
    }

    @Override
    public Uni<List<SearchDataDto>> search(
            SqlConnection conn, Filter filter, Integer offset, Integer limit) {
        List<SearchDataDto> dtos = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            dtos.add(createDto(i));
        }
        return Uni.createFrom().item(dtos);
    }

    private SearchDataDto createDto(int index) {
        JsonNodeFactory factory = objectMapper.getNodeFactory();
        SearchDataDto dto = new SearchDataDto();
        dto.setId((long) index);
        dto.setLiveUrl("http://localhost:808%s/dpp%s".formatted(index, index));
        dto.setUpi("%s2345".formatted(index));
        ObjectNode node = factory.objectNode();
        node.set("productName", factory.textNode("product%s".formatted(index)));
        node.set("weight", factory.numberNode(5.34 * index));
        dto.setData(node);
        return dto;
    }
}
