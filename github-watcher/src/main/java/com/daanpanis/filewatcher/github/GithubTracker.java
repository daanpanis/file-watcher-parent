package com.daanpanis.filewatcher.github;

import com.daanpanis.filewatcher.AsyncWatcherProcess;
import com.daanpanis.filewatcher.FileTracker;

import java.io.IOException;

public class GithubTracker extends FileTracker {

    private final AsyncWatcherProcess process = new AsyncWatcherProcess("Github-Watcher-Thread", new GithubTrackerProcess(this), 5000);

    public GithubTracker() throws IOException {
        super(new String[]{"github", "ghub"});
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
