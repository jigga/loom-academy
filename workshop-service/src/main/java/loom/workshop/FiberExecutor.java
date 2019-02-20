package loom.workshop;

import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class FiberExecutor implements ThreadPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(FiberExecutor.class);

    private final ExecutorService scheduler =
        Executors.newFixedThreadPool(10);

    @PostConstruct
    void init() {
        if (scheduler instanceof ThreadPoolExecutor) {
            LOGGER.info("Pre-starting core threads...");
            ((ThreadPoolExecutor) scheduler).prestartAllCoreThreads();
        }
    }

    @PreDestroy
    void cleanUp() throws InterruptedException {
        var tasks = scheduler.shutdownNow();
        LOGGER.info("Tasks in the queue during shutdown: {}", tasks);
        join();
    }

    @Override
    public void join() throws InterruptedException {
        LOGGER.info("Waiting for the thread pool to shut down...");
        scheduler.shutdownNow();
        scheduler.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Override
    public int getThreads() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIdleThreads() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLowOnThreads() {
        return false;
    }

    /**
     * Right now we execute the given task (or HTTP request to be more precise, as this is the thread pool that we
     * customize the Jetty container with) by submitting it to the preconfigured {@link #scheduler}.
     *
     * Your task is to change the implementation of this method, so that it executes the given request using
     * {@link Fiber} and the preconfigured {@link #scheduler}.
     *
     * @param command The command/request/task to execute.
     */
    @Override
    public void execute(Runnable command) {
//        scheduler.execute(command);
        Fiber.schedule(scheduler, command);
    }

}
