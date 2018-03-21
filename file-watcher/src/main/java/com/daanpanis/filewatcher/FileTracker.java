package com.daanpanis.filewatcher;

import java.util.ArrayList;
import java.util.Collection;

public abstract class FileTracker {

    protected final Collection<TrackerRule> rules = new ArrayList<>();
    private final String[] names;

    public FileTracker(String[] names) {
        this.names = names;
    }

    public String[] getNames() {
        return names;
    }

    public abstract void startAsync();

    public abstract void stop();

    public void addRule(TrackerRule rule) {
        this.rules.add(rule);
    }

    public Collection<TrackerRule> getRules() {
        return rules;
    }
}
