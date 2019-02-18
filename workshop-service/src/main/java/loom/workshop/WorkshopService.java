package loom.workshop;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

@RestController
@EnableAsync
@EnableDiscoveryClient
@SpringBootApplication
public class WorkshopService {

    public static void main(String[] args) {
        SpringApplication.run(WorkshopService.class);
    }

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(1000);
        return executor;
    }

    // an executor that schedules @Async methods to run in fibers rather than threads
    @Bean(name = "fiberTaskExecutor")
    public Executor fiberTaskExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(1000);
        return executor;
    }

}

@RestController
@RequestMapping("/workshop")
class WorkshopController {

    private final EchoClient service;

    public WorkshopController(EchoClient service) {
        this.service = service;
    }

    @GetMapping("/v1")
    public CompletableFuture<JsonNode> getWithDelayWithThread() {
        return service.getWithDelayWithThread();
    }

    @GetMapping("/v2")
    public CompletableFuture<JsonNode> getWithDelayWithFiber() {
        return service.getWithDelayWithFiber();
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
