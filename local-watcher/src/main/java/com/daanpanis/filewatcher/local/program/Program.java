package com.daanpanis.filewatcher.local.program;

import com.daanpanis.filewatcher.*;
import com.daanpanis.filewatcher.local.LocalTracker;

import java.util.Collection;

public class Program {

    public static boolean running = true;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
       /* WatchService watcher = FileSystems.getDefault().newWatchService();
        Path dir = Paths.get("C:/Users/Daan/Desktop/Commands.");
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

        while (running) {
            key.pollEvents().forEach(event -> {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind == OVERFLOW) {
                    return;
                }

                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filename = ev.context();
                System.out.println(kind.name() + ": " + FilenameUtils.getName(filename.toString()));
            });
        }*/
        /*TrackerRule rule = new TrackerRule(new CommandScriptHandler(), "C:/Users/Daan/Desktop/Commands");
        rule.addMatcher(new FolderMatcher("/Test/"));
        rule.addMatcher(new FolderMatcher("/"));
        LocalTracker tracker = new LocalTracker();
        tracker.addRule(rule);
        tracker.startAsync();*/
        FileWatchers watchers = new FileWatchers();
        watchers.registerFileTracker(new LocalTracker());
        watchers.registerUpdateHandler(new CommandScriptHandler());

        watchers.loadConfiguration(Program.class.getResourceAsStream("/test.json"));

        watchers.getRegisteredTracker("local").startAsync();
    }

    public static class CommandScriptHandler implements UpdateHandler {

        @Override
        public void onUpdated(Collection<? extends TrackedFile> files) {
            System.out.println("Updated:");
            files.forEach(file -> System.out.println(" - " + file.getFullPath()));
        }

        @Override
        public void onRemoved(Collection<? extends TrackedFile> files) {
            System.out.println("Removed:");
            files.forEach(file -> System.out.println(" - " + file.getFullPath()));
        }

        @Override
        public void onAdded(Collection<? extends TrackedFile> files) {
            System.out.println("Added:");
            files.forEach(file -> System.out.println(" - " + file.getFullPath()));
        }

        @Override
        public String[] getNames() {
            return new String[]{"test"};
        }
    }

}
