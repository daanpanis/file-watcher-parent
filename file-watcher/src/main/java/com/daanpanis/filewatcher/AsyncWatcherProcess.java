package com.daanpanis.filewatcher;

public class AsyncWatcherProcess {

    private final String name;
    private final Runnable runnable;
    private final long intervalMillis;
    private boolean running = true;
    private Thread thread;

    public AsyncWatcherProcess(String name, Runnable runnable, long intervalMillis) {
        this.name = name;
        this.runnable = runnable;
        this.intervalMillis = intervalMillis;
    }

    public void start() {
        if (thread == null) {
            running = true;
            thread = new Thread(() -> {
                while (running) {
                    runnable.run();
                    try {
                        Thread.sleep(intervalMillis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        thread.interrupt();
                        break;
                    }
                }
            }, name);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            running = false;
            thread.interrupt();
            thread = null;
        }
    }
}
