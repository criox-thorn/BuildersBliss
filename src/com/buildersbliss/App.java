package com.buildersbliss;

import java.io.IOException;
import java.util.Properties;
import java.io.FileInputStream;

import com.buildersbliss.io.JarExtractor;


public class App {
    public static void main(String[] args) {
        Properties settings = new Properties();
        String extFile = "";

        try (FileInputStream settingsFile = new FileInputStream("config.properties")) {
            settings.load(settingsFile);
            extFile = settings.getProperty("folderPath");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading settings file.");
        }

        try {
            JarExtractor.extract("C:\\Users\\***REMOVED***\\Documents\\BuildersBliss test files\\chipped-neoforge-1.21.1-4.0.2.jar", extFile);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error Extracting Jar");
        }
    }
}
