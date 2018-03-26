package com.daanpanis.filewatcher.github;

import com.daanpanis.filewatcher.CredentialsParser;
import com.daanpanis.filewatcher.FileTracker;
import com.daanpanis.filewatcher.exceptions.CredentialsParseException;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Set;

public class GithubCredentialsParser implements CredentialsParser<GithubCredentials> {

    @Override
    public GithubCredentials parse(JsonObject json) throws CredentialsParseException {
        Set<String> users = getUsers(json);
        return getCredentials(json, users);
    }

    private GithubCredentials getCredentials(JsonObject json, Set<String> users) throws CredentialsParseException {
        GithubCredentials credentials = null;
        if (json.has("username") && json.has("access_token")) {
            credentials = new GithubCredentials(json.get("access_token").getAsString(), json.get("username").getAsString(), null, users);
        }
        if (json.has("username") && json.has("password")) {
            credentials = new GithubCredentials(null, json.get("username").getAsString(), json.get("password").getAsString(), users);
        }
        if (credentials == null) throw new CredentialsParseException("Either need an access token or a username and password");
        return credentials;
    }

    private Set<String> getUsers(JsonObject json) throws CredentialsParseException {
        if (!json.has("users")) throw new CredentialsParseException("No users defined");
        if (!json.get("users").isJsonArray()) throw new CredentialsParseException("Users not array");
        Set<String> users = new HashSet<>();
        json.get("users").getAsJsonArray().forEach(element -> {
            if (element.isJsonPrimitive()) users.add(element.getAsString().toLowerCase());
        });
        return users;
    }

    @Override
    public String[] getNames() {
        return new String[]{"github", "ghub"};
    }

    @Override
    public Class<? extends FileTracker<GithubCredentials>> getTrackerClass() {
        return GithubTracker.class;
    }

    @Override
    public Class<GithubCredentials> getCredentialsClass() {
        return GithubCredentials.class;
    }
}
