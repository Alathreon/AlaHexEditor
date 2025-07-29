package com.alathreon.alahexeditor.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class RoutineManager {
    private final ScheduledExecutorService executor;
    public RoutineManager() {
        executor = Executors.newScheduledThreadPool(1);
    }
    public void schedule() {

    }
}
