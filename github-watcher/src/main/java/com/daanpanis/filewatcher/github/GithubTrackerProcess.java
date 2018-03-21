package com.daanpanis.filewatcher.github;

import com.daanpanis.filewatcher.FolderMatcher;
import com.daanpanis.filewatcher.TrackerRule;
import com.daanpanis.filewatcher.utilities.FileUtils;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import org.kohsuke.github.*;
import org.kohsuke.github.extras.OkHttpConnector;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GithubTrackerProcess implements Runnable {

    private final GithubTracker tracker;
    private final GitHub github;
    private final Map<String, RepositoryEntry> trackedRepositories = new HashMap<>();

    public GithubTrackerProcess(GithubTracker tracker) throws IOException {
        this.tracker = tracker;
        Cache cache = new Cache(new File("C:/Users/Daan/Desktop/cache"), 10 * 1024 * 1024); // 10MB cache
        this.github = new GitHubBuilder().withPassword("dpdaan@hotmail.com", Password.github)
                .withConnector(new OkHttpConnector(new OkUrlFactory(new OkHttpClient().setCache(cache)))).build();
    }

    @Override
    public void run() {
        CompletableFuture[] tasks = tracker.getRules().stream().map(rule -> CompletableFuture.runAsync(() -> {
            Difference difference = getDifference(rule);
            rule.removed(difference.removed);
            rule.added(difference.added);
            rule.updated(difference.updated);
        })).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(tasks).join();
    }

    private Difference getDifference(TrackerRule rule) {
        String repositoryId = rule.getLocation();
        Difference difference = new Difference();
        try {
            if (trackedRepositories.containsKey(repositoryId)) {
                checkUpdateDifferences(difference, trackedRepositories.get(repositoryId));
            } else {
                GHRepository repository = github.getRepository(repositoryId);
                getInitialFiles(rule.getMatchers().stream().map(FolderMatcher::getFolder).collect(Collectors.toList()), repository, difference,
                        "master");
            }
        } catch (IOException e) {
            // TODO Log
        }
        return difference;
    }

    private void checkUpdateDifferences(Difference difference, RepositoryEntry entry) {
        GHRepository repository = entry.repository;
        GHCommit lastCommit = getLastCommit(repository, entry.branch);
        if (lastCommit != null) {
            try {
                GHCompare compare = repository.getCompare(entry.commit, lastCommit);
                String base = repository.getFullName();
                for (GHCommit.File changedFile : compare.getFiles()) {
                    if (changedFile.getStatus().equalsIgnoreCase("removed")) {
                        difference.removed(new GithubFile(repository.getFileContent(changedFile.getFileName(), entry.commit.getSHA1()), base));
                    } else if (changedFile.getStatus().equalsIgnoreCase("modified")) {
                        difference.updated(new GithubFile(repository.getFileContent(changedFile.getFileName(), lastCommit.getSHA1()), base));
                    } else if (changedFile.getStatus().equalsIgnoreCase("added")) {
                        difference.added(new GithubFile(repository.getFileContent(changedFile.getFileName(), lastCommit.getSHA1()), base));
                    }
                }
                entry.commit = lastCommit;
            } catch (IOException e) {
                // TODO Log
            }
        }
    }

    private void getInitialFiles(List<String> folders, GHRepository repository, Difference difference, String branchName) {
        RepositoryEntry entry = new RepositoryEntry(repository, branchName);
        entry.commit = getLastCommit(repository, branchName);
        trackedRepositories.put(repository.getFullName(), entry);
        folders.forEach(folder -> {
            try {
                repository.getDirectoryContent(FileUtils.normalizePath(folder), branchName).stream().filter(GHContent::isFile)
                        .forEach(file -> difference.added(new GithubFile(file, repository.getFullName())));
            } catch (IOException e) {
                // TODO Log
            }
        });
    }

    private GHCommit getLastCommit(GHRepository repository, String ref) {
        List<GHCommit> commits = repository.queryCommits().from(ref).pageSize(1).list().asList();
        return !commits.isEmpty() ? commits.get(0) : null;
    }


    private class Difference {

        private final Collection<GithubFile> removed = new ArrayList<>();
        private final Collection<GithubFile> added = new ArrayList<>();
        private final Collection<GithubFile> updated = new ArrayList<>();

        public Difference added(GithubFile file) {
            this.added.add(file);
            return this;
        }

        public Difference removed(GithubFile file) {
            this.removed.add(file);
            return this;
        }

        public Difference updated(GithubFile file) {
            this.updated.add(file);
            return this;
        }
    }

    private class RepositoryEntry {

        private final GHRepository repository;
        private final String branch;
        private GHCommit commit;

        private RepositoryEntry(GHRepository repository, String branch) {
            this.repository = repository;
            this.branch = branch;
        }
    }

}
