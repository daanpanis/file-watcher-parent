package com.daanpanis.filewatcher.local;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalTrackerProcess implements Runnable {

    private final LocalTracker tracker;
    private final Map<String, TrackedEntry> trackedModifiedDates = new HashMap<>();

    public LocalTrackerProcess(LocalTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void run() {
        tracker.getRules().forEach(rule -> {
            rule.getMatchers().stream().map(matcher -> Paths.get(rule.getLocation(), matcher.getFolder()).toString()).forEach(folder -> {
                Map<LocalFile, Diff> differences = getDifferences(Paths.get(folder), rule.getLocation());
                rule.added(differences.entrySet().stream().filter(entry -> entry.getValue() == Diff.ADDED).map(Map.Entry::getKey)
                        .collect(Collectors.toList()));
                rule.removed(differences.entrySet().stream().filter(entry -> entry.getValue() == Diff.REMOVED).map(Map.Entry::getKey)
                        .collect(Collectors.toList()));
                rule.updated(differences.entrySet().stream().filter(entry -> entry.getValue() == Diff.UPDATED).map(Map.Entry::getKey)
                        .collect(Collectors.toList()));
            });
        });
    }

    public Map<LocalFile, Diff> getDifferences(Path folderPath, String basePath) {
        Map<LocalFile, Diff> differences = new HashMap<>();
        trackedModifiedDates.entrySet().removeIf(entry -> {
            TrackedEntry tracked = entry.getValue();
            if (!Paths.get(tracked.file.getFile().getAbsolutePath()).startsWith(folderPath)) return false;
            if (!tracked.file.getFile().exists()) {
                differences.put(tracked.file, Diff.REMOVED);
                return true;
            }
            if (tracked.file.lastModified().isAfter(tracked.trackedModified)) {
                tracked.trackedModified = tracked.file.lastModified();
                differences.put(tracked.file, Diff.UPDATED);
            }
            return false;
        });
        File folder = new File(folderPath.toString());
        if (folder.exists()) {
            Stream.of(Objects.requireNonNull(folder.listFiles(file -> !file.isDirectory()))).forEach(file -> {
                if (!trackedModifiedDates.containsKey(file.getAbsolutePath())) {
                    LocalFile localFile = new LocalFile(file, basePath);
                    differences.put(localFile, Diff.ADDED);
                    trackedModifiedDates.put(file.getAbsolutePath(), new TrackedEntry(localFile));
                }
            });
        }
        return differences;
    }

    public enum Diff {
        ADDED,
        UPDATED,
        REMOVED
    }

    public class TrackedEntry {

        private final LocalFile file;
        private LocalDateTime trackedModified;

        public TrackedEntry(LocalFile file) {
            this.file = file;
            this.trackedModified = file.lastModified();
        }
    }
}
