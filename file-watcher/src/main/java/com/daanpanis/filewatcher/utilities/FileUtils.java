package com.daanpanis.filewatcher.utilities;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static final String SEPARATOR = "/";

    public static Path normalizePath(Path path) {
        return Paths.get(normalizePath(path.toString()));
    }

    public static String normalizePath(String path) {
        path = path.replace(SEPARATOR + "{2,}", SEPARATOR);
        if (path.startsWith(SEPARATOR)) path = path.substring(1);
        if (!path.endsWith(SEPARATOR)) path = path + SEPARATOR;
        return path;
    }

    public static String getExtension(String path) {
        int i = path.lastIndexOf('.');
        if (i > 0) {
            return path.substring(i + 1);
        }
        return "";
    }

    public static Path getExtension(Path path) {
        return Paths.get(getExtension(path.toString()));
    }

    public static Path getRelative(String path, String basePath) {
        return getRelative(Paths.get(path), Paths.get(basePath));
    }

    public static Path getRelative(Path path, Path basePath) {
        return basePath.relativize(path);
    }

    private FileUtils() {
    }

}
