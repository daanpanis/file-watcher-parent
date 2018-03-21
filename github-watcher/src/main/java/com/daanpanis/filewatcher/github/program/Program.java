package com.daanpanis.filewatcher.github.program;

import com.daanpanis.filewatcher.FolderMatcher;
import com.daanpanis.filewatcher.TrackedFile;
import com.daanpanis.filewatcher.TrackerRule;
import com.daanpanis.filewatcher.UpdateHandler;
import com.daanpanis.filewatcher.github.GithubTracker;
import com.daanpanis.filewatcher.utilities.FileUtils;

import java.util.Collection;

public class Program {

    public static void main(String[] args) throws Exception {
       /* Cache cache = new Cache(new File("C:/Users/Daan/Desktop/cache"), 10 * 1024 * 1024);
        GitHub github = new GitHubBuilder().withPassword("dpdaan@hotmail.com", Password.github)
                .withConnector(new OkHttpConnector(new OkUrlFactory(new OkHttpClient().setCache(cache)))).build();
        GHRepository repository = github.getRepository("daanpanis/test-github");
        List<GHCommit> commits = repository.listCommits().asList();
        GHCommit commit = commits.get(1);
        GHCommit commit2 = commits.get(2);
        for (GHCommit.File file : repository.getCompare(commit2, commit).getFiles()) {
            System.out.println(file.getStatus());
        }
        System.out.println(repository.getFileContent("README.md", commit2.getSHA1()));*/
        /*List<GHContent> contents = repository.getDirectoryContent("test/", commit.getSHA1());
        System.out.println(repository.getFullName());
        contents.forEach(content -> {
            System.out.println(content.getName());
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(content.read()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });*/
       /* TrackerRule rule = new TrackerRule(new CommandScriptHandler(), "daanpanis/test-github");
        rule.addMatcher(new FolderMatcher("/"));
        rule.addMatcher(new FolderMatcher("/test/"));
        GithubTracker tracker = new GithubTracker();
        tracker.addRule(rule);
        tracker.startAsync();*/
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
