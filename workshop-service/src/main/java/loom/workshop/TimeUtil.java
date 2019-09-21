package loom.workshop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeUtil.class);

    private TimeUtil() {}

    public static void measureTime(Runnable command) {
        LOGGER.info("Executing command: {}", command);
        var start = System.nanoTime();
        command.run();
        var end = System.nanoTime();
        LOGGER.info("Command {} executed in {} seconds", command, ((end - start)/1e9));
    }

}
