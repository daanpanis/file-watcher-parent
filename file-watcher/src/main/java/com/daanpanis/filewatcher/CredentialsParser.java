package com.daanpanis.filewatcher;

import com.daanpanis.filewatcher.exceptions.CredentialsParseException;
import com.google.gson.JsonObject;

public interface CredentialsParser<T> {

    T parse(JsonObject json) throws CredentialsParseException;

    String[] getNames();

    Class<? extends FileTracker<T>> getTrackerClass();

    Class<T> getCredentialsClass();

}
