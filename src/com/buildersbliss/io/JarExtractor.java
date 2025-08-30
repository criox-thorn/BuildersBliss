package com.buildersbliss.io;

import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.Enumeration;

import com.buildersbliss.model.Block;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import static com.buildersbliss.model.ColourUtils.colourAverager;
import static com.buildersbliss.model.ColourUtils.rgbToLab;


public class JarExtractor {
    private static final Gson gson = new Gson();
    static HashMap<String, Block> blockHash = new HashMap<>();

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
            throw new IOException(folderName + " already exits !!!");
        }
        boolean modFolderCreationSuccess = modFolder.mkdirs();
        if (!modFolderCreationSuccess) {
            System.err.println("Failed to Create Mod Folder");
        }

        HashMap<String, Block> tempBlockHash = new HashMap<>();

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

                    // Textures json file structure
                    JsonObject texturesJson = gson.fromJson(blockJson.get("textures"), JsonObject.class);



                    // Accessing Block Data
                    try {
                        String parent = blockJson.get("parent").getAsString();
                        if (parent.contains("door")) {
                            throw new NullPointerException();
                        }

                        String all = "None";
                        String side = "None";
                        String top = "None";
                        String bottom = "None";
                        String end = "None";
                        if (texturesJson.has("all")) {
                            all = texturesJson.get("all").getAsString();
                        }
                        if (texturesJson.has("side")) {
                            side = texturesJson.get("side").getAsString();
                        }
                        if (texturesJson.has("top")) {
                            top = texturesJson.get("top").getAsString();
                        }
                        if (texturesJson.has("bottom")) {
                            bottom = texturesJson.get("bottom").getAsString();
                        }
                        if (texturesJson.has("end")) {
                            end = texturesJson.get("end").getAsString();
                        }

                        if (all == "None" && side == "None" && top == "None" && bottom == "None" && end == "None") {
                            throw new NullPointerException("Not standard texture types");
                        }

                        double[] Lab = new double[3];

                        // Creating Block Folder
                        File blockFolder = new File(modFolder.getPath() + "\\" + blockName);
                        if (blockFolder.exists()) {
                            return;
                        }
                        boolean blockFolderCreationSuccess = blockFolder.mkdirs();
                        if (!blockFolderCreationSuccess) {
                            System.err.println("Failed to Create Block Folder for " + blockFolder.getPath());
                        }
                        extractTextures(all, top, bottom, side, end, jarFile, blockFolder.getPath());

                        double[] allLab = new double[3];
                        double[] topLab = new double[3];
                        double[] bottomLab = new double[3];
                        double[] sideLab = new double[3];
                        double[] endLab = new double[3];

                        for (int i = 0; i < blockFolder.listFiles().length; i++) {
                            if (!blockFolder.listFiles()[i].isDirectory()) {
                                Color avgColours = colourAverager(blockFolder.listFiles()[i]);
                                double[] lab = rgbToLab(avgColours);

                                if (blockFolder.listFiles()[i].getPath().contains("\\all.png")) {
                                    allLab = lab;
                                }
                                if (blockFolder.listFiles()[i].getPath().contains("\\top.png")) {
                                    topLab = lab;
                                }
                                if (blockFolder.listFiles()[i].getPath().contains("\\bottom.png")) {
                                    bottomLab = lab;
                                }
                                if (blockFolder.listFiles()[i].getPath().contains("\\side.png")) {
                                    sideLab = lab;
                                }
                                if (blockFolder.listFiles()[i].getPath().contains("\\end.png")) {
                                    endLab = lab;
                                }
                            }
                        }

                        //Finished Block Console Print
                        System.out.println(String.format("COMPLETED | Block ID: %s:%s | Parent: %s | Image All: %s | Image Side: %s | Image Top: %s | Image Bottom: %s |  Image End: %s | Texture Faces: %s | All LAB: %s | Top LAB: %s | Bottom LAB: %s | Side LAB: %s | End LAB: %s", folderName, blockName, parent, all, side, top, bottom, end, texturesJson.keySet(), Arrays.toString(allLab), Arrays.toString(topLab), Arrays.toString(bottomLab), Arrays.toString(sideLab), Arrays.toString(endLab)));
                        Block block = new Block(parent, blockFolder.getPath(), Arrays.stream(texturesJson.keySet().toArray()).map(obj -> (obj != null) ? obj.toString() : "null").toArray(String[]::new), allLab, topLab, bottomLab, sideLab, endLab);
                        tempBlockHash.put(folderName + ":" + blockName, block);
                    } catch (NullPointerException e) {
                        // For non-standard blocks
                    }
                }
            }
        }
        blockHash.putAll(tempBlockHash);
    }
    public static String createJSON() throws IOException {
        Gson blockJSON = new GsonBuilder().setPrettyPrinting().create();
        String filePath = "blocks.json";
        File modJSON = new File(filePath);
        try (FileWriter writer = new FileWriter(modJSON)) {
            blockJSON.toJson(blockHash, writer);
            System.out.println("Saved blocks.json with " + blockHash.size() + " entries");
            return filePath;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static File extractTexture(JarFile jarfile, String fp, File outputFile) {
        try {
            JarEntry entry = jarfile.getJarEntry(fp);
            if (entry == null) {
                System.err.println("Texture not found in JAR: " + fp);
                return null;
            }
            try (InputStream in = jarfile.getInputStream(entry);
                 FileOutputStream out = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesRead);
                }
                return outputFile;
            }
        }
        catch (Exception e) {
            System.err.println("Failure during Image Extraction");
            e.printStackTrace();
            System.exit(0);
            return null;
        }

    }
    public static void extractTextures(String all, String top, String bottom, String side, String end, JarFile jarfile, String blockPath)  {
        if (!Objects.equals(all, "None")) {
            String texturePath = "assets/" + all.replace(":", "/textures/") + ".png";
            File outputFile = new File(blockPath + "\\all.png" );
            extractTexture(jarfile, texturePath, outputFile);
        }
        if (!Objects.equals(top, "None")) {
            String texturePath = "assets/" + top.replace(":", "/textures/") + ".png";
            File outputFile = new File(blockPath + "\\top.png" );
            extractTexture(jarfile, texturePath, outputFile);
        }
        if (!Objects.equals(bottom, "None")) {
            String texturePath = "assets/" + bottom.replace(":", "/textures/") + ".png";
            File outputFile = new File(blockPath + "\\bottom.png" );
            extractTexture(jarfile, texturePath, outputFile);
        }
        if (!Objects.equals(side, "None")) {
            String texturePath = "assets/" + side.replace(":", "/textures/") + ".png";
            File outputFile = new File(blockPath + "\\side.png" );
            extractTexture(jarfile, texturePath, outputFile);
        }
        if (!Objects.equals(end, "None")) {
            String texturePath = "assets/" + end.replace(":", "/textures/") + ".png";
            File outputFile = new File(blockPath + "\\end.png" );
            extractTexture(jarfile, texturePath, outputFile);
        }
    }

    public static String textureJarPath(String referncePath) {
        System.out.println(referncePath);
        return referncePath;
    }
}
