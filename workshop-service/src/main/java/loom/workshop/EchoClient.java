package loom.workshop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class EchoClient {

    private static final Logger logger = LoggerFactory.getLogger(EchoClient.class);

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final HttpClient httpClient;

    public EchoClient(DiscoveryClient discoveryClient, RestTemplateBuilder restTemplateBuilder, ObjectMapper mapper) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = restTemplateBuilder.build();
        this.mapper = mapper;
        this.httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build();
    }

    public JsonNode getWithDelay() {
        return getWithDelayWithHttpClient();
    }

    private JsonNode getWithDelayWithRestTemplate() {
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
