package loom.workshop;

//import org.apache.catalina.connector.Connector;
//import org.apache.catalina.core.StandardThreadExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//@Component
public class TomcatCustomizer implements ApplicationContextAware, WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TomcatCustomizer.class);

    private ApplicationContext applicationContext;
    private Executor scheduler;

//    @PostConstruct
//    void init() throws Exception {
//        this.scheduler = new StandardThreadExecutor();
//        ((StandardThreadExecutor) this.scheduler).start();
//    }
//
//    @PreDestroy
//    void destroy() throws Exception {
//        ((StandardThreadExecutor) this.scheduler).stop();
//    }

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        /*
        var executor = applicationContext.getBean(FiberExecutor.class);
        var connectorCustomizer = new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
//                var scheduler = new ThreadPoolExecutor(20, 200, 5, TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000)) {
//                    @Override
//                    public void execute(Runnable command) {
//                        Fiber.schedule(command);
//                    }
//                };
                LOGGER.info("Current executor name: {}", connector.getExecutorName());
                LOGGER.info("Current executor is: {}", connector.getProtocolHandler().getExecutor());
                connector.getProtocolHandler().setExecutor(scheduler);
            }
        };
        factory.addConnectorCustomizers(connectorCustomizer);
        LOGGER.info("Tomcat customized...");
        LOGGER.info("Customizing Tomcat...");
         */
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
