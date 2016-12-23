package com.coding.sony;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexTextReplacementInFiles {

    public static Pattern pattern = null;
    public static Map<String,Integer> replacementCount = new HashMap<>();
    public static int numFileProcessed = 0;
    public static int totalReplacements = 0;
    
    public static void process(String startingDir, String regexPattern, String replacement, String fileAcceptPattern) {
        File file = new File(startingDir);
        walkAndProcess(file, regexPattern, replacement, fileAcceptPattern);
        
        // print output
        System.out.println("Processed " + numFileProcessed + " files.  Repalced to \"" + replacement + "\"");
        for (Map.Entry<String,Integer> entry : replacementCount.entrySet()) {
            System.out.println("* " + entry.getKey() + " : " + entry.getValue() + " occurances");
        }
        System.out.println("totalReplacements=" + totalReplacements);
    }

    private static void walkAndProcess(File file, String regexPattern, String replacement, String fileAcceptPattern) {
        if (!file.exists())
            return;
        else if (file.isDirectory()) {
            // list the files and call walkAndProcess recursively
            File[] list = file.listFiles();
            for (File f : list)
                walkAndProcess(f, regexPattern, replacement, fileAcceptPattern);
        }
        else if (file.isFile())
            replace(file, regexPattern, replacement, fileAcceptPattern);
    }
    
    private static void replace (File file, String regexPattern, String replacement, String fileAcceptPattern) {
        // need to see if file matches fileAcceptPattern
        String filename = file.getName();
        if (null != fileAcceptPattern && !filename.matches(fileAcceptPattern)) {
            //System.out.println("skipping " + file);
            return;
        }
        
        numFileProcessed++;
        // open an output file, read each line, replace and write
        String outFilename = file + ".processed";
        //System.out.println("replacing " + file + ", write to " + outFilename);
        try (BufferedReader br = new BufferedReader(new FileReader(file));
                BufferedWriter bw = new BufferedWriter(new FileWriter(outFilename))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                // find match
                Matcher m = pattern.matcher(line);
                while(m.find()) {
                    String matchedString = m.group();
                    replacementCount.compute(matchedString, 
                            (k,v) -> v == null ? 1 : v+1
                            );
                    totalReplacements++;
                }
                String replacedLine = m.replaceAll(replacement);
                // replace
                bw.write(replacedLine + "\n");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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
        
        // regexPattern must be legal
        try {
            pattern = Pattern.compile(regexPattern);
            Matcher m = pattern.matcher("test");
            if (m.groupCount() != 1) {
                System.out.println("number of groups is not 1, it is " + m.groupCount());
                System.exit(1);
            }
        } catch (PatternSyntaxException pse) {
            System.err.println("Regex syntax error: " + pse.getMessage());
            System.exit(1);
        }
        
        if (startingDir != null) {
            process(startingDir, regexPattern, replacement, fileAcceptPattern);
        } else {
            System.out.println("Expected at least 3 parameters but got " + args.length);
        }
    }
}

