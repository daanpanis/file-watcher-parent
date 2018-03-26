package com.daanpanis.filewatcher.github;

import com.daanpanis.filewatcher.FolderMatcher;
import com.daanpanis.filewatcher.TrackerRule;
import com.daanpanis.filewatcher.utilities.FileUtils;
import org.kohsuke.github.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GithubTrackerProcess implements Runnable {

    private final GithubTracker tracker;
    private final Map<String, RepositoryEntry> trackedRepositories = new HashMap<>();
    private final Map<String, GitHub> accountConnections = new HashMap<>();

    public GithubTrackerProcess(GithubTracker tracker) {
        this.tracker = tracker;
        //        Cache cache = new Cache(new File("C:/Users/Daan/Desktop/cache"), 10 * 1024 * 1024); // 10MB cache
        //        this.github = new GitHubBuilder().withPassword("dpdaan@hotmail.com", Password.github)
        //                .withConnector(new OkHttpConnector(new OkUrlFactory(new OkHttpClient().setCache(cache)))).build();
        //        this.github = GitHub.connectUsingPassword("dpdaan@hotmail.com", Password.github);
    }

    @Override
    public void run() {
        try {
            resolveAccountConnections();
            Async.runParallel(tracker.getRules().stream().map(rule -> (Runnable) () -> {
                Difference difference = getDifference(rule);
                rule.removed(difference.removed);
                rule.added(difference.added);
                rule.updated(difference.updated);
                //                System.out.println("Done calling updates");
            }).collect(Collectors.toList())).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void resolveAccountConnections() throws InterruptedException {
        Async.runParallel(tracker.getCredentials().stream().map(credentials -> (Runnable) () -> {
            try {
                GitHub connection = credentials.buildConnection();
                credentials.getForUsers().forEach(user -> accountConnections.put(user.toLowerCase(), connection));
            } catch (IOException e) {
                e.printStackTrace();
                // TODO Log
            }
        }).collect(Collectors.toList())).await();
    }

    private Difference getDifference(TrackerRule rule) {
        String repositoryId = rule.getLocation();
        //        System.out.println("Getting diff: " + repositoryId);
        Difference difference = new Difference();
        try {
            if (trackedRepositories.containsKey(repositoryId)) {
                checkUpdateDifferences(difference, trackedRepositories.get(repositoryId));
            } else {
                GitHub connection = getConnection(getUser(repositoryId));
                if (connection != null) {
                    GHRepository repository = connection.getRepository(repositoryId);
                    getInitialFiles(rule.getMatchers().stream().map(FolderMatcher::getFolder).collect(Collectors.toList()), repository, difference,
                            "master");
                }
            }
        } catch (IOException e) {
            //            e.printStackTrace();
            // TODO Log
        }
        return difference;
    }

    private GitHub getConnection(String user) {
        return accountConnections.get(user.toLowerCase());
    }

    private String getUser(String repositoryId) {
        if (repositoryId != null) return repositoryId.split("/")[0];
        return "";
    }

    private void checkUpdateDifferences(Difference difference, RepositoryEntry entry) {
        //System.out.println("checking update differences");
        GHRepository repository = entry.repository;
        GHCommit lastCommit = getLastCommit(repository, entry.branch);
        if (lastCommit != null) {
            //System.out.println("Old: " + entry.commit.getSHA1());
            //System.out.println("Current: " + lastCommit.getSHA1());
            try {
                GHCompare compare = repository.getCompare(entry.commit, lastCommit);
                //System.out.println("Changed files: " + compare.getFiles().length);
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
                //                e.printStackTrace();
                // TODO Log
            }
        }
    }

    private void getInitialFiles(List<String> folders, GHRepository repository, Difference difference, String branchName) {
        //System.out.println("Getting initial files");
        RepositoryEntry entry = new RepositoryEntry(repository, branchName);
        entry.commit = getLastCommit(repository, branchName);
        trackedRepositories.put(repository.getFullName(), entry);
        folders.forEach(folder -> {
            //System.out.println("Folder: " + folder);
            try {
                repository.getDirectoryContent(FileUtils.normalizePath(folder), branchName).stream().filter(GHContent::isFile)
                        .forEach(file -> difference.added(new GithubFile(file, repository.getFullName())));
            } catch (IOException e) {
                //                e.printStackTrace();
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
