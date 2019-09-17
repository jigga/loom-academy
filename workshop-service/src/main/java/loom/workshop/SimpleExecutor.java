package loom.workshop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
public class SimpleExecutor implements Executor {

    private final Logger logger = LoggerFactory.getLogger(SimpleExecutor.class);
    private final Executor scheduler =
        Executors.newSingleThreadExecutor();
    // private final FiberScope poolScope = FiberScope.open();
    private final boolean runInFiber;

    public SimpleExecutor(@Value("${run.in.fiber}") boolean runInFiber) {
        this.runInFiber = runInFiber;
    }

    public void execute(Runnable command) {
        Runnable runnable = () -> {
            measureTime(command);
        };
        if (runInFiber) {
            FiberScope.background().schedule(scheduler, runnable);
        } else {
            scheduler.execute(runnable);
        }
    }

    void measureTime(Runnable command) {
        MDC.put("requestId", UUID.randomUUID().toString());
        try {
            var start = System.nanoTime();
            command.run();
            var end = System.nanoTime();
            logger.info("Command {} executed in {} seconds", command, ((end - start)/1e9));
        } finally {
            MDC.clear();
        }
    }

}
