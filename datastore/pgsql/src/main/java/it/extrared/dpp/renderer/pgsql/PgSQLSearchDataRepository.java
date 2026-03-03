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
package it.extrared.dpp.renderer.pgsql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.arc.Unremovable;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.sqlclient.Row;
import it.extrared.dpp.renderer.business.search.filter.SQLFilterVisitor;
import it.extrared.dpp.renderer.repository.AbstractSearchDataRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.function.Function;

/** An implementation of an {@link AbstractSearchDataRepository} supporting PgSQL storage. */
@Unremovable
@ApplicationScoped
public class PgSQLSearchDataRepository extends AbstractSearchDataRepository {

    @Inject ObjectMapper objectMapper;

    @Override
    protected Function<Row, JsonNode> jsonMapper() {
        return Unchecked.function(
                r ->
                        objectMapper.convertValue(
                                (((JsonObject) r.getJson("search_data")).getMap()),
                                JsonNode.class));
    }

    @Override
    protected SQLFilterVisitor getFilterVisitor() {
        return new PgSQLFilterVisitor();
    }
}
