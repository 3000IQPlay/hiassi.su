package me.hollow.sputnik.api.util.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtil {

    public static void submitToExecutor(Runnable runnable) {
        final ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(runnable);
        executor.shutdown();
    }

}
