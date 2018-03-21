package com.daanpanis.filewatcher.github;

import com.daanpanis.filewatcher.TrackedFile;
import com.daanpanis.filewatcher.utilities.FileUtils;
import org.kohsuke.github.GHContent;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class GithubFile implements TrackedFile {

    private final LocalDateTime date;
    private final GHContent file;
    private final String base;

    public GithubFile(GHContent file, String base) {
        this.date = LocalDateTime.now();
        this.file = file;
        this.base = FileUtils.normalizePath(base);
    }

    @Override
    public Path getFullPath() {
        return Paths.get(file.getPath());
    }

    @Override
    public String getBase() {
        return base;
    }

    @Override
    public LocalDateTime lastModified() {
        return date;
    }

    @Override
    public InputStream getInput() throws IOException {
        return file.read();
    }
}
