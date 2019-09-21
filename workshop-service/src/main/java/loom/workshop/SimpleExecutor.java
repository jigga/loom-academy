package loom.workshop;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
public class SimpleExecutor implements Executor {

    private final boolean runInFiber;
    private final Executor scheduler;
    private ThreadLocal<FiberScope> fiberScope = ThreadLocal.withInitial(FiberScope::open);

    public SimpleExecutor(@Value("${run.in.fiber}") boolean runInFiber) {
        this.runInFiber = runInFiber;
        var threadFactoryBuilder = new ThreadFactoryBuilder();
        if (this.runInFiber) {
            threadFactoryBuilder.setNameFormat("fiber-carrier-thread-%d");
        }
        scheduler = Executors.newFixedThreadPool(4, threadFactoryBuilder.build());
    }

    public void execute(Runnable command) {
        if (runInFiber) {
            Runnable task = () -> {
                MDC.put("requestId", RequestId.get());
                TimeUtil.measureTime(command);
            };
            fiberScope.get().schedule(scheduler, task);
        } else {
            Runnable task = () -> {
                MDC.put("requestId", UUID.randomUUID().toString());
                try {
                    TimeUtil.measureTime(command);
                } finally {
                    MDC.clear();
                }
            };
            scheduler.execute(task);
        }
    }

}
