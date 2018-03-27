package com.daanpanis.filewatcher.local;

import com.daanpanis.filewatcher.AsyncWatcherProcess;
import com.daanpanis.filewatcher.FileTracker;

public class LocalTracker extends FileTracker<Object> {

    private final AsyncWatcherProcess process = new AsyncWatcherProcess("Local-Watcher-Thread", new LocalTrackerProcess(this), 5000);

    public LocalTracker() {
        super(new String[]{"local"});
    }

    @Override
    public void startAsync() {
        process.start();
    }

    @Override
    public void stop() {
        process.stop();
    }
}
