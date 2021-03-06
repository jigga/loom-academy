package loom.echo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/echo")
@EnableDiscoveryClient
@SpringBootApplication
public class EchoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EchoService.class);

    public static void main(String[] args) {
        SpringApplication.run(EchoService.class);
    }

    @RequestMapping(
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        method = {
            RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS
        }
    )
    public ResponseEntity<Map<String, Object>> echo(HttpServletRequest request) {

        var headers = new HashMap<>();
        for (String headerName : Collections.list(request.getHeaderNames())) {
            headers.put(headerName, request.getHeader(headerName));
        }

        var responseMap = new HashMap<String,Object>();
        responseMap.put("protocol", request.getProtocol());
        responseMap.put("method", request.getMethod());
        responseMap.put("headers", headers);
        responseMap.put("cookies", request.getCookies());
        responseMap.put("parameters", request.getParameterMap());
        responseMap.put("path", request.getServletPath());

        LOGGER.info("Returning: {}", responseMap);

        return ResponseEntity.status(HttpStatus.OK).body(responseMap);

    }

    @RequestMapping(
        value = "/delay/{delay}",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        method = {
            RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS
        }
    )
    public ResponseEntity<Map<String, Object>> echoWithDelay(@PathVariable Integer delay, HttpServletRequest request) throws InterruptedException {

        TimeUnit.MILLISECONDS.sleep(delay);

        return echo(request);

    }

    @Component
    public static class TomcatCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
        @Override
        public void customize(TomcatServletWebServerFactory factory) {
            factory.addConnectorCustomizers(connector -> connector.getProtocolHandler().setExecutor(command -> {
                FiberScope.background().schedule(command);
            }));
        }
    }

}
