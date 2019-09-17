package loom.workshop;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class EchoClient {

    private final DiscoveryClient discoveryClient;
    private final HttpClient httpClient;

    public EchoClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        this.httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build();
    }

    public String getWithDelay() {
        return getWithDelayWithHttpClient();
    }

    private String getWithDelayWithHttpClient() {
        var request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(getEchoServiceUri()))
            .timeout(Duration.ofSeconds(30))
            .header("Accept", "application/json")
            .build();
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getEchoServiceUri() {
        // up to 1 sec
        var delay = ThreadLocalRandom.current().nextInt(1001);
        var echoServiceInstances = discoveryClient.getInstances("echo-service");
        Collections.shuffle(echoServiceInstances);
        return echoServiceInstances.get(0).getUri() + "/echo/delay/" + delay;
    }

}
