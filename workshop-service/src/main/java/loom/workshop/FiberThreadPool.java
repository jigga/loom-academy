package loom.workshop;

//import org.eclipse.jetty.util.thread.ThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FiberThreadPool /*implements ThreadPool*/ {

    private final ExecutorService scheduler =
        Executors.newFixedThreadPool(10);
    private final FiberScope poolScope = FiberScope.open();

//    @Override
    public void join() throws InterruptedException {
        poolScope.close();
        scheduler.shutdownNow();
        scheduler.awaitTermination(10, TimeUnit.SECONDS);
    }

//    @Override
    public int getThreads() {
        return 10;
    }

//    @Override
    public int getIdleThreads() {
        return 0;
    }

//    @Override
    public boolean isLowOnThreads() {
        return false;
    }

//    @Override
    public void execute(Runnable command) {
        poolScope.schedule(command);
    }
}
