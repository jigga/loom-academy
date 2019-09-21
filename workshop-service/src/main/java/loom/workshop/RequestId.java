package loom.workshop;

import java.util.UUID;

public class RequestId {

    private static final ThreadLocal<String> requestId = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());

    public static String get() {
        return requestId.get();
    }

}
