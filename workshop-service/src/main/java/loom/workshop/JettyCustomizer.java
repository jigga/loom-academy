package loom.workshop;

//import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

@Component
public class JettyCustomizer implements WebServerFactoryCustomizer<JettyServletWebServerFactory> {

    @Value("${run.in.fiber}")
    private boolean runInFiber;

    @Override
    public void customize(JettyServletWebServerFactory factory) {
        /*
        factory.setThreadPool(new ThreadPool() {
            private final Executor scheduler = ForkJoinPool.commonPool();
            private final FiberScope poolScope = FiberScope.open();

            @Override
            public void join() throws InterruptedException {
                poolScope.close();
            }

            @Override
            public int getThreads() {
                return 1;
            }

            @Override
            public int getIdleThreads() {
                return 0;
            }

            @Override
            public boolean isLowOnThreads() {
                return false;
            }

            @Override
            public void execute(Runnable command) {
                if (runInFiber) {
                    poolScope.schedule(scheduler, () -> {
                        TimeUtil.measureTime(command);
                    });
                } else {
                    scheduler.execute(command);
                }
            }
        });
         */
    }



}
