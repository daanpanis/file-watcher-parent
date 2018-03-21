package com.daanpanis.filewatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class TrackerRule {

    private final UpdateHandler handler;
    private final String location;
    private final Collection<FolderMatcher> matchers = new ArrayList<>();

    public TrackerRule(UpdateHandler handler, String location) {
        this.handler = handler;
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public Collection<FolderMatcher> getMatchers() {
        return matchers;
    }

    public void addMatcher(FolderMatcher matcher) {
        this.matchers.add(matcher);
    }

    public void added(Collection<? extends TrackedFile> files) {
        files = filterFiles(files);
        if (!files.isEmpty()) handler.onAdded(files);
    }

    public void updated(Collection<? extends TrackedFile> files) {
        files = filterFiles(files);
        if (!files.isEmpty()) handler.onUpdated(files);
    }

    public void removed(Collection<? extends TrackedFile> files) {
        files = filterFiles(files);
        if (!files.isEmpty()) handler.onRemoved(files);
    }

    private Collection<? extends TrackedFile> filterFiles(Collection<? extends TrackedFile> files) {
        return files.stream().filter(file -> matchers.stream().anyMatch(matcher -> matcher.matches(file))).collect(Collectors.toList());
    }
}
