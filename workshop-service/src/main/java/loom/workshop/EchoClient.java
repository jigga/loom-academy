package loom.workshop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class EchoClient {

    private static final Logger logger = LoggerFactory.getLogger(EchoClient.class);

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final Random random;
    private final HttpClient httpClient;

    public EchoClient(DiscoveryClient discoveryClient, RestTemplateBuilder restTemplateBuilder, ObjectMapper mapper) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = restTemplateBuilder.build();
        this.mapper = mapper;
        this.random = new Random();
        this.httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build();
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<JsonNode> getWithDelayWithThread() {
        return CompletableFuture.completedFuture(getWithDelayWithHttpClient());
    }

    @Async("fiberTaskExecutor")
    public CompletableFuture<JsonNode> getWithDelayWithFiber() {
        return CompletableFuture.completedFuture(getWithDelayWithHttpClient());
    }

    private JsonNode getWithLocalDelay() {
        try {
            TimeUnit.MILLISECONDS.sleep(random.nextInt(1001));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        var node = mapper.createObjectNode();
        node.put("status", "OK");
        node.put("time", System.currentTimeMillis());
        return node;
    }

    private JsonNode getWithDelay() {
        return restTemplate.getForObject(getEchoServiceUri(), JsonNode.class);
    }

    private JsonNode getWithDelayWithHttpClient() {
        var request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(getEchoServiceUri()))
            .timeout(Duration.ofSeconds(30))
            .header("Accept", "application/json")
            .build();
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            return mapper.readValue(response.body(), JsonNode.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getEchoServiceUri() {
        // up to 1 sec
        var delay = ThreadLocalRandom.current().nextInt(1001);
        logger.info("Delay for the request is: {} ms", delay);
        var echoServiceInstances = discoveryClient.getInstances("echo-service");
        Collections.shuffle(echoServiceInstances);
        var uri = echoServiceInstances.get(0).getUri() + "/echo/delay/" + delay;
        logger.info("Delay service uri is: {}", uri);
        return uri;
    }

}
