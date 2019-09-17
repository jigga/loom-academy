package loom.workshop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class TimeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeUtil.class);

    private TimeUtil() {}

    public static void measureTime(Runnable command) {
        var start = System.nanoTime();
        command.run();
        var end = System.nanoTime();
        LOGGER.info("Command {} executed in {} seconds", command, ((end - start)/1e9));
    }

    public static void main(String[] args) throws Exception {
        int result = CompletableFuture.supplyAsync(() -> 1)
            .thenApplyAsync(i -> i * 2)
            .thenApplyAsync(i -> i + 10)
            .thenApplyAsync(i -> i/0)
            .completeAsync(() -> 100)
            .get();
        System.out.println(result);
    }

}
