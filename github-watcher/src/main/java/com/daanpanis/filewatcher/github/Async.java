package com.daanpanis.filewatcher.github;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

public class Async {

    public static CountDownLatch runParallel(Collection<Runnable> tasks) {
        CountDownLatch latch = new CountDownLatch(tasks.size());
        tasks.forEach(task -> new Thread(() -> {
            try {
                task.run();
            } finally {
                latch.countDown();
            }
        }).start());
        return latch;
    }


}
