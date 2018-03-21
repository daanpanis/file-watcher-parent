package com.daanpanis.filewatcher;

import com.daanpanis.filewatcher.utilities.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;

import java.nio.file.Paths;

public class FolderMatcher implements TrackerMatcher {

    private final String folder;

    public FolderMatcher(String folder) {
        this.folder = folder;
    }

    public String getFolder() {
        return folder;
    }

    @Override
    public boolean matches(TrackedFile file) {
        //        System.out.println("Folder: " + folder);
        //        System.out.println("Folder path: " + FileUtils.normalizePath(folder));
        //        System.out.println("File path: " + FileUtils.normalizePath(file.getPath()));
        //        System.out.println("Path: " + FilenameUtils.getFullPath(file.getPath()));
        //        System.out.println(
        //                "Equals: " + FilenameUtils.equalsNormalizedOnSystem(FilenameUtils.getFullPath(folder), FilenameUtils.getFullPath(file
        // .getPath())));
        //        return FilenameUtils.equalsNormalizedOnSystem(FileUtils.normalizePath(folder), FileUtils.normalizePath(file.getPath())/*, true,
        // IOCase.SYSTEM*/);

        return Paths.get(FileUtils.normalizePath(folder)).compareTo(FileUtils.normalizePath(file.getPath())) == 0;
    }
}
