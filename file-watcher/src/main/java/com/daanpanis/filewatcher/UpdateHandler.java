package com.daanpanis.filewatcher;

import java.util.Collection;

public interface UpdateHandler {

    void onUpdated(Collection<? extends TrackedFile> files);

    void onRemoved(Collection<? extends TrackedFile> files);

    void onAdded(Collection<? extends TrackedFile> files);

    String[] getNames();

}
