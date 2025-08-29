package com.buildersbliss.io;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.Enumeration;

import com.buildersbliss.model.Block;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


public class JarExtractor {
    private static final Gson gson = new Gson();

    public static void extract(String jarPath, String extPath) throws java.io.IOException {
        // Getting Mod Name
        int lastForwardSlash = jarPath.lastIndexOf("\\");
        int nameLen = jarPath.length();
        String folderName = jarPath.substring(0, nameLen - 4).substring(lastForwardSlash + 1);

        JarFile jarFile = new JarFile(new File(jarPath));
        Enumeration<JarEntry> en = jarFile.entries();

        // Creating Mod Folder in folderPath
        File modFolder = new File(extPath + "\\" + folderName);
        if (modFolder.exists()) {
            return;
        }
        boolean modFolderCreationSuccess = modFolder.mkdirs();
        if (!modFolderCreationSuccess){
            System.err.println("Failed to Create Mod Folder");
        }

        //iterating for each file in extract
        while (en.hasMoreElements()) {
            JarEntry je = en.nextElement();
            // Isolating .json block files
            if (!je.isDirectory() && je.getName().contains("/models/block/") && je.getName().contains(".json")) {
                System.out.println("Reading: " + je.getName());

                try (InputStream is = jarFile.getInputStream(je);
                    InputStreamReader reader = new InputStreamReader(is)) {
                    // Reading block .json file & retrieving block name
                    JsonObject blockJson = gson.fromJson(reader, JsonObject.class);
                    int blockNameIndexBackslash = je.getName().lastIndexOf("/");
                    int blockNameLen = je.getName().length();
                    String blockName = je.getName().substring(blockNameIndexBackslash + 1, blockNameLen - 5);

                    // Creating Block Folder
                    File blockFolder = new File(modFolder.getPath() + "\\" + blockName);
                    if (blockFolder.exists()) {
                        return;
                    }
                    boolean blockFolderCreationSuccess = blockFolder.mkdirs();
                    if (!blockFolderCreationSuccess){
                        System.err.println("Failed to Create Block Folder for " + blockFolder.getPath());
                    }

                    // Textures json file structure
                    JsonObject texturesJson = gson.fromJson(blockJson.get("textures"), JsonObject.class);

                    // Accessing Block Data
                    try {
                        String parent = blockJson.get("parent").getAsString();

                        //Finished Block Console Print
                        System.out.println(String.format("Block ID: %s:%s | Parent: %s | Original Image Path: %s | New Image Path: s | Texture Faces: %s | LAB: d, d, d", folderName, blockName, parent, "placeholder", texturesJson.keySet() ));
                    }
                    catch (NullPointerException e) {
                        // For non-standard blocks
                        System.err.println("Not Standard Block Type");
                    }
                }
            }
        }

        System.out.println("Extraction Finished on " + folderName);
        return;
    }
}
