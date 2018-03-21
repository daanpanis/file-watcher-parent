package com.daanpanis.filewatcher;

public interface TrackerMatcher {

    boolean matches(TrackedFile file);

}
