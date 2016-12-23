package com.coding.sony;

import java.io.File;

public class RegexTextReplacementInFiles {
    
    public static void process(String startingDir, String regexPattern, String replacement, String fileAcceptPattern) {
        File file = new File(startingDir);
        walkAndProcess(file, regexPattern, replacement, fileAcceptPattern);
    }

    private static void walkAndProcess(File file, String regexPattern, String replacement, String fileAcceptPattern) {
        if (file.isDirectory()) {
            // list the files and call walkAndProcess recursively
            File[] list = file.listFiles();
            for (File f : list)
                walkAndProcess(f, regexPattern, replacement, fileAcceptPattern);
        }
        else 
            replace(file, regexPattern, replacement, fileAcceptPattern);
    }
    
    private static void replace (File file, String regexPattern, String replacement, String fileAcceptPattern) {
        // need to see if file matches fileAcceptPattern
        System.out.print("checking " + file + ": ");
        String filename = file.getName();
        if (null == fileAcceptPattern || filename.matches(fileAcceptPattern))
            System.out.println("replacing " + file);
        else
            System.out.println();
    }
    
    public static void main(String[] args) {
        String startingDir = null
                , regexPattern = null
                , replacement = null
                , fileAcceptPattern = null
                ;
        
        if (args.length >= 3) {
            startingDir = args[0];
            regexPattern = args[1];
            replacement = args[2];
        }
        if (args.length >= 4) {
            fileAcceptPattern = args[3];
            // change fileAcceptPattern to string regex
            fileAcceptPattern = fileAcceptPattern.replace(".","\\.");
            fileAcceptPattern = fileAcceptPattern.replace("*", ".*");
            fileAcceptPattern = fileAcceptPattern.replace("?", ".?");
        }
        if (startingDir != null) {
            process(startingDir, regexPattern, replacement, fileAcceptPattern);
        } else {
            System.out.println("Expected at least 3 parameters but got " + args.length);
        }
    }
}
