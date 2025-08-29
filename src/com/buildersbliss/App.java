package com.buildersbliss;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Properties;
import java.io.FileInputStream;

import com.buildersbliss.io.JarExtractor;
import com.buildersbliss.model.ColourUtils;
import static com.buildersbliss.core.GradientGenerator.gradient;
import static com.buildersbliss.model.ColourUtils.*;


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
            JarExtractor.extract("chipped-neoforge-1.21.1-4.0.2.jar", extFile);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error Extracting Jar");
        }

        ArrayList gradientTest = gradient(new double[]{63,46,35},new double[]{11,30,-23},6);


        System.out.println(deltaE2000(randomColourGenerator(), randomColourGenerator()));

    }
}
