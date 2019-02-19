package loom.workshop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class JettyCustomizer implements ApplicationContextAware, WebServerFactoryCustomizer<JettyServletWebServerFactory> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JettyCustomizer.class);

    private ApplicationContext applicationContext;

    @Override
    public void customize(JettyServletWebServerFactory factory) {
        var scheduler = applicationContext.getBean(FiberExecutor.class);
        factory.setThreadPool(scheduler);
        LOGGER.info("Jetty customized with {}", scheduler);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
