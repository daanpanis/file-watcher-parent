package com.daanpanis.filewatcher;

import java.util.ArrayList;
import java.util.Collection;

public abstract class FileTracker<T> {

    protected final Collection<TrackerRule> rules = new ArrayList<>();
    protected final Collection<T> credentials = new ArrayList<>();
    private final String[] names;

    public FileTracker(String[] names) {
        this.names = names;
    }

    public String[] getNames() {
        return names;
    }

    public void addCredentials(T credentials) {
        this.credentials.add(credentials);
    }

    public Collection<T> getCredentials() {
        return credentials;
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
