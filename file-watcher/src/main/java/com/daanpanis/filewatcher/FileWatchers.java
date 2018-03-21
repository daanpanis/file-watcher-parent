package com.daanpanis.filewatcher;

import com.daanpanis.filewatcher.exceptions.ConfigurationLoadException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FileWatchers {

    private final Map<String, FileTracker> registeredTrackers = new HashMap<>();
    private final Map<String, UpdateHandler> registeredHandler = new HashMap<>();
    private final JsonParser parser = new JsonParser();

    public void registerFileTracker(FileTracker fileTracker) {
        for (String name : fileTracker.getNames()) {
            registeredTrackers.put(name.toLowerCase(), fileTracker);
        }
    }

    public boolean isTrackerRegistered(String name) {
        return registeredTrackers.containsKey(name.toLowerCase());
    }

    public FileTracker getRegisteredTracker(String name) {
        return registeredTrackers.get(name.toLowerCase());
    }

    public void registerUpdateHandler(UpdateHandler handler) {
        for (String name : handler.getNames()) {
            registeredHandler.put(name.toLowerCase(), handler);
        }
    }

    public boolean isHandlerRegistered(String name) {
        return registeredHandler.containsKey(name.toLowerCase());
    }

    public UpdateHandler getRegisteredHandler(String name) {
        return registeredHandler.get(name.toLowerCase());
    }

    public void loadConfiguration(File file) throws FileNotFoundException, ConfigurationLoadException {
        loadConfiguration(new FileInputStream(file));
    }

    public void loadConfiguration(InputStream inputStream) throws ConfigurationLoadException {
        loadConfiguration(parser.parse(new InputStreamReader(inputStream)));
    }


    public void loadConfiguration(JsonElement element) throws ConfigurationLoadException {
        if (!element.isJsonObject()) throw new ConfigurationLoadException("Not json object");
        JsonObject json = element.getAsJsonObject();
        if (json.has("watchers")) loadWatchers(json.get("watchers"));
    }

    private void loadWatchers(JsonElement element) throws ConfigurationLoadException {
        if (!element.isJsonArray()) throw new ConfigurationLoadException("Watchers aren't array");
        for (JsonElement jsonElement : element.getAsJsonArray()) { loadWatcher(jsonElement); }
    }

    private void loadWatcher(JsonElement element) throws ConfigurationLoadException {
        if (!element.isJsonObject()) throw new ConfigurationLoadException("Watcher entry not json object");
        JsonObject json = element.getAsJsonObject();
        if (!json.has("type")) throw new ConfigurationLoadException("No type defined");
        if (!json.has("location")) throw new ConfigurationLoadException("No location defined");
        if (!json.has("rules")) throw new ConfigurationLoadException("No rules defined");
        loadRules(json.get("type").getAsString(), json.get("location").getAsString(), json.get("rules"));
    }

    private void loadRules(String type, String location, JsonElement rulesElement) throws ConfigurationLoadException {
        if (!rulesElement.isJsonArray()) throw new ConfigurationLoadException("rules must be an array");
        JsonArray rules = rulesElement.getAsJsonArray();
        FileTracker tracker = getTracker(type);
        getRules(rules, location).forEach(tracker::addRule);
    }

    private FileTracker getTracker(String type) throws ConfigurationLoadException {
        if (isTrackerRegistered(type)) return getRegisteredTracker(type);
        throw new ConfigurationLoadException("Tracker with name '" + type + "' isn't registered");
    }

    private Collection<TrackerRule> getRules(JsonArray rulesArray, String location) throws ConfigurationLoadException {
        Collection<TrackerRule> rules = new ArrayList<>();
        for (JsonElement rule : rulesArray) {
            rules.add(getRule(rule, location));
        }
        return rules;
    }

    private TrackerRule getRule(JsonElement element, String location) throws ConfigurationLoadException {
        if (!element.isJsonObject()) throw new ConfigurationLoadException("Rule entry isn't json object");
        JsonObject json = element.getAsJsonObject();
        if (!json.has("handler")) throw new ConfigurationLoadException("No handler defined");
        if (!json.has("matchers")) throw new ConfigurationLoadException("No matchers defined");
        UpdateHandler handler = getHandler(json.get("handler").getAsString());
        TrackerRule rule = new TrackerRule(handler, location);
        getMatchers(json.get("matchers")).forEach(rule::addMatcher);
        return rule;
    }

    private UpdateHandler getHandler(String handler) throws ConfigurationLoadException {
        if (isHandlerRegistered(handler)) return getRegisteredHandler(handler);
        throw new ConfigurationLoadException("Unregistered handler '" + handler + "'");
    }

    private Collection<FolderMatcher> getMatchers(JsonElement matchersElement) throws ConfigurationLoadException {
        if (!matchersElement.isJsonArray()) throw new ConfigurationLoadException("Matchers aren't an array!");
        Collection<FolderMatcher> matchers = new ArrayList<>();
        for (JsonElement element : matchersElement.getAsJsonArray()) {
            if (element.isJsonPrimitive()) matchers.add(new FolderMatcher(element.getAsString()));
        }
        return matchers;
    }


}
