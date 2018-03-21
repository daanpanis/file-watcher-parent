package com.daanpanis.filewatcher.program;

import com.daanpanis.filewatcher.utilities.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;

public class Program {

    public static void main(String[] args) {
        String folder = "/commands/";
        String fileName = "Commands/kappa.groovy";
        System.out.println(FilenameUtils.getPath(folder));
        System.out.println(FilenameUtils.getPath(fileName));
        System.out.println(FilenameUtils.equals(FilenameUtils.getPath(folder), FilenameUtils.getPath(fileName), true, IOCase.SYSTEM));
        System.out.println(FileUtils.normalizePath("C:/Users/Daan/Desktop"));
    }

}
