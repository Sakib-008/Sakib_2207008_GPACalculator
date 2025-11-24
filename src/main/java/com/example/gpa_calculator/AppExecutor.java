package com.example.gpa_calculator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutor {
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    public static ExecutorService getExecutor() {
        return executor;
    }

    public static void shutdown() {
        executor.shutdown();
    }
}
