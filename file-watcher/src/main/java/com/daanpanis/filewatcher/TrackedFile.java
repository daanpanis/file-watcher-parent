package com.daanpanis.filewatcher;

import com.daanpanis.filewatcher.utilities.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public interface TrackedFile {

    default Path getName() {
        return getFullPath().getFileName();
    }

    default Path getPath() {
        return getFullPath().getParent() != null ? getFullPath().getParent() : Paths.get("");
    }

    Path getFullPath();

    String getBase();

    default String getExtension() {
        return FileUtils.getExtension(getName().toString());
    }

    LocalDateTime lastModified();

    InputStream getInput() throws IOException;

}
