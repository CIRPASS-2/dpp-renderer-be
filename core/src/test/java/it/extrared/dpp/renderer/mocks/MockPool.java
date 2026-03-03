package it.extrared.dpp.renderer.mocks;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.SqlConnection;
import java.util.function.Function;
import org.mockito.Mockito;

public class MockPool extends Pool {

    public MockPool() {
        super(Mockito.mock(io.vertx.sqlclient.Pool.class));
    }

    @Override
    public <T> Uni<T> withTransaction(Function<SqlConnection, Uni<T>> function) {
        return function.apply(Mockito.mock(SqlConnection.class));
    }

    @Override
    public <T> Uni<T> withConnection(Function<SqlConnection, Uni<T>> function) {
        return function.apply(Mockito.mock(SqlConnection.class));
    }
}
