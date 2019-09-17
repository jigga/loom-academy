package loom.workshop;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class TomcatCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TomcatCustomizer.class);

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        LOGGER.info("Customizing Tomcat...");
        /*
         * Turns out that Tomcat processes incoming requests using NioEndpoint$SocketProcessor
         * (https://github.com/wenzhucjy/tomcat_source/blob/master/tomcat-8.0.9-sourcecode/java/org/apache/tomcat/util/net/NioEndpoint.java)
         * which extends SocketProcessorBase, whose run method is synchronized.
         */
        var connectorCustomizer = new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                LOGGER.info("Connector class: {}", connector.getClass());
                /*
                 * This was an attempt to close FiberScope associated with Executor...
                 */
                connector.addLifecycleListener(event -> {
                    LOGGER.info("Event: {}, type: {}", event, event.getType());
                });
                LOGGER.info("Current executor name: {}", connector.getExecutorName());
                LOGGER.info("Current executor is: {}", connector.getProtocolHandler().getExecutor());
                connector.getProtocolHandler().setExecutor(new SimpleExecutor(true));
            }
        };
        factory.addConnectorCustomizers(connectorCustomizer);

        var connector =
            new Connector();
        connector.setPort(8081);
        connector.getProtocolHandler().setExecutor(new SimpleExecutor(false));
        factory.addAdditionalTomcatConnectors(connector);

        LOGGER.info("Tomcat customized...");
    }

}
