package it.extrared.dpp.renderer.business;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.MultiMap;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import java.io.IOException;
import java.io.InputStream;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

public class TestUtils {

    public static final String TEST_VOCABULARY_URL = "http://dpp.taltech.ee/EUDPP";

    public static void configureWebClientReqResp(
            WebClient webClient, String fileName, String argMatch, String respContentType) {
        @SuppressWarnings("unchecked")
        HttpRequest<Buffer> request = Mockito.mock(HttpRequest.class);
        Mockito.when(request.headers()).thenAnswer(inv -> MultiMap.caseInsensitiveMultiMap());
        Mockito.doReturn(Uni.createFrom().item(mockResponse(fileName, respContentType)))
                .when(request)
                .send();

        Mockito.doReturn(request)
                .when(webClient)
                .getAbs(
                        ArgumentMatchers.argThat(
                                (ArgumentMatcher<String>) url -> url.contains(argMatch)));
    }

    static HttpResponse<Buffer> mockResponse(String fileName, String cType) {
        @SuppressWarnings("unchecked")
        HttpResponse<Buffer> response = Mockito.mock(HttpResponse.class);

        // Anche qui: nuovo MultiMap per ogni chiamata
        Mockito.when(response.headers()).thenAnswer(inv -> MultiMap.caseInsensitiveMultiMap());

        Mockito.when(response.getHeader(ArgumentMatchers.eq("Content-Type"))).thenReturn(cType);

        Buffer respBody = Buffer.buffer(resourceAsByteArray(fileName));
        Mockito.when(response.bodyAsBuffer()).thenReturn(respBody);

        return response;
    }

    static byte[] resourceAsByteArray(String fileName) {
        try (InputStream is = TestUtils.class.getResourceAsStream("/dpp/%s".formatted(fileName))) {
            return is != null ? is.readAllBytes() : new byte[0];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
