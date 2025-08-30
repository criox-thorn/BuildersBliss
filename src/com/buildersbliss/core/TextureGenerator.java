package com.buildersbliss.core;

import com.buildersbliss.io.BlockMatch;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static com.buildersbliss.io.BlockMatch.match;

public class TextureGenerator {
    public static List<BlockMatch> blockMatchingTexture(String blockID, String side, int amount, String jsonPath) {
        Gson gson = new Gson();
        try {
            JsonObject block = gson.fromJson(new FileReader(jsonPath), JsonObject.class).getAsJsonObject(blockID);
            JsonArray labJSON = block.getAsJsonArray(side+"Lab");
            double [] colour = new double[] {
                    labJSON.get(0).getAsDouble(),
                    labJSON.get(1).getAsDouble(),
                    labJSON.get(2).getAsDouble()
            };

            List<BlockMatch> textureMatch = match(colour, jsonPath, amount + 1);
            textureMatch.removeIf(match -> match.blockId.equals(blockID) && match.face.equals(side));

            for (BlockMatch bloc : textureMatch) {

                System.out.println(bloc.blockId + " | " + bloc.face + " | " + bloc.distance);
            }

            return textureMatch;

        }
        catch (Exception e) {
            System.err.println("Cannot Complete Texture !!!");
            e.printStackTrace();
        }
        return null;
    }
}
