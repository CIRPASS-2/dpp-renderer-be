package it.extrared.dpp.renderer.mocks;

import static it.extrared.dpp.renderer.business.TestUtils.configureWebClientReqResp;

import io.quarkus.arc.properties.IfBuildProperty;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.sqlclient.Pool;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import org.mockito.Mockito;

@ApplicationScoped
public class MockProducer {

    @Produces
    @IfBuildProperty(name = "test.mock.pool", stringValue = "true", enableIfMissing = true)
    @ApplicationScoped
    public Pool pool() {
        return new MockPool();
    }

    @Produces
    @Alternative
    @Priority(1)
    @ApplicationScoped
    public WebClient webClient() {
        WebClient webClient = Mockito.mock(WebClient.class);
        configureWebClientReqResp(
                webClient, "dpp-smartphone-mod.json", "json-mod-ld", "application/ld+json");
        configureWebClientReqResp(
                webClient, "dpp-smartphone.json", "json-ld", "application/ld+json");
        configureWebClientReqResp(webClient, "dpp-laptop.xml", "rdf-xml", "application/rdf+xml");
        configureWebClientReqResp(
                webClient, "dpp-laptop-mod.xml", "rdf-mod-xml", "application/rdf+xml");
        configureWebClientReqResp(webClient, "dpp-battery.ttl", "rdf-ttl", "application/x+turtle");
        configureWebClientReqResp(webClient, "dpp-shoes.nt", "rdf-nt", "application/n-triples");
        configureWebClientReqResp(webClient, "dpp-fridge.n3", "rdf-n3", "text/n3");
        configureWebClientReqResp(webClient, "dpp-hoven.json", "plain-json", "application/json");
        return webClient;
    }
}
