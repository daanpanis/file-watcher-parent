package com.daanpanis.filewatcher.local;

import com.daanpanis.filewatcher.TrackedFile;
import com.daanpanis.filewatcher.utilities.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class LocalFile implements TrackedFile {

    private final File file;
    private final Path fullPath;
    private final String base;

    public LocalFile(File file, String base) {
        this.file = file;
        this.base = FileUtils.normalizePath(base);
        this.fullPath = FileUtils.getRelative(file.getAbsolutePath(), base);
    }

    @Override
    public Path getFullPath() {
        return fullPath;
    }

    @Override
    public String getBase() {
        return base;
    }

    @Override
    public LocalDateTime lastModified() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), TimeZone.getDefault().toZoneId());
    }

    @Override
    public InputStream getInput() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public File getFile() {
        return file;
    }
}
