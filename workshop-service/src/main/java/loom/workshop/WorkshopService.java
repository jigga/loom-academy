package loom.workshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@EnableAsync
@EnableDiscoveryClient
@SpringBootApplication
public class WorkshopService {

    public static void main(String[] args) {
        SpringApplication.run(WorkshopService.class);
    }

}

@RestController
@RequestMapping("/loom")
class WorkshopController {

    private final EchoClient service;

    public WorkshopController(EchoClient service) {
        this.service = service;
    }

    @GetMapping(value = "/test", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getWithDelayWithThread() {
        return service.getWithDelay();
    }

    @GetMapping(value = "/gc", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String gc() {
        System.gc();
        return "{\"status\": \"OK\"}";
    }

}

@RestController
class ServiceInstanceRestController {

    private final DiscoveryClient discoveryClient;

    public ServiceInstanceRestController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @GetMapping("/services")
    public List<String> serviceInstancesByApplicationName() {
        return discoveryClient.getServices();
    }

    @GetMapping("/services/{serviceName}")
    public List<ServiceInstance> serviceInstancesByApplicationName(@PathVariable String serviceName) {
        return discoveryClient.getInstances(serviceName);
    }

}
