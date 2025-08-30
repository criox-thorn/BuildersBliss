package com.buildersbliss;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.io.FileInputStream;
import java.util.Arrays;

import com.buildersbliss.io.BlockMatch;
import com.buildersbliss.io.JarExtractor;

import static com.buildersbliss.core.GradientGenerator.blockMatchingGradient;
import static com.buildersbliss.core.PaletteGenerator.blockMatchingPalette;
import static com.buildersbliss.core.TextureGenerator.blockMatchingTexture;
import static com.buildersbliss.io.JarExtractor.createJSON;


public class App {
    public static void main(String[] args) {
        Properties settings = new Properties();
        String extFile = "";
        String jarFolderPath = "";

        try (FileInputStream settingsFile = new FileInputStream("config.properties")) {
            settings.load(settingsFile);
            extFile = settings.getProperty("folderPath");
            jarFolderPath = settings.getProperty("jarFolderPath");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading settings file.");
        }

        String jsonPath = "blocks.json";


        File jarFolder = new File(jarFolderPath);
        System.out.println("Found Files in " + jarFolderPath + ": " + Arrays.toString(jarFolder.list()));

        try {
            for (String jarFile : jarFolder.list()) {
                if (jarFile.endsWith(".jar")) {
                    JarExtractor.extract(jarFolder.getAbsolutePath() + "\\" + jarFile, extFile);
                }
            }
            jsonPath = createJSON();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error Extracting Jar");
        }

        ArrayList<List<BlockMatch>> blockMatchedGradient = blockMatchingGradient(new double[]{63,46,35}, new double[]{80,12,82}, 7, 5, jsonPath);
        ArrayList<List<BlockMatch>> blockMatchedPalette = blockMatchingPalette(7, 5, jsonPath);
        List<BlockMatch> blockMatchedTexture = blockMatchingTexture("chipped-neoforge-1.21.1-4.0.2:gray_terracotta_tiles", "all", 20, jsonPath);
    }
}
