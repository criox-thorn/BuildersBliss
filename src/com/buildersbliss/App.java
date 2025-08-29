package com.buildersbliss;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.io.FileInputStream;
import java.util.Arrays;

import com.buildersbliss.io.BlockMatcher;
import com.buildersbliss.io.JarExtractor;
import com.buildersbliss.model.ColourUtils;
import static com.buildersbliss.core.GradientGenerator.gradient;
import static com.buildersbliss.io.BlockMatcher.match;
import static com.buildersbliss.io.JarExtractor.createJSON;
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

        String jsonPath = "blocks.json";

        try {
            JarExtractor.extract("chipped-neoforge-1.21.1-4.0.2.jar", extFile);
            jsonPath = createJSON();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error Extracting Jar");
        }



        ArrayList<double[]> gradientTest = gradient(new double[]{63,46,35},new double[]{11,30,-23},6);

        for (double[] arr : gradientTest) {
            System.out.println(Arrays.toString(arr));
        }

        String[] colourMatch = match(new double[]{63.0, 46.0, 35.0}, jsonPath, 10);

        System.out.println(Arrays.toString(colourMatch));

    }
}
