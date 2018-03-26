package com.daanpanis.filewatcher.github;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.util.Collection;

public class GithubCredentials {

    private final String accessToken;
    private final String userName;
    private final String password;
    private final Collection<String> forUsers;

    public GithubCredentials(String accessToken, String userName, String password, Collection<String> forUsers) {
        this.accessToken = accessToken;
        this.userName = userName;
        this.password = password;
        this.forUsers = forUsers;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Collection<String> getForUsers() {
        return forUsers;
    }

    public GitHub buildConnection() throws IOException {
        GitHubBuilder builder = new GitHubBuilder();
        if (accessToken != null) {
            builder.withOAuthToken(accessToken, userName);
        } else if (password != null) {
            builder.withPassword(userName, password);
        }
        return builder.build();
    }
}
