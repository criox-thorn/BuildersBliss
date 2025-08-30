package com.buildersbliss.io;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.io.FileReader;
import java.util.Comparator;
import java.util.List;

import static com.buildersbliss.model.ColourUtils.deltaE2000;

public class BlockMatch {
    public String blockId;
    public String face;
    public double distance;

    public BlockMatch(String blockId, String face, double distance) {
        this.blockId = blockId;
        this.face = face;
        this.distance = distance;
    }

    private String getBlockID(){
        return this.blockId;
    }

    public static List match(double[] lab, String jsonPath, int amount) {
        Gson gson = new Gson();
        List<BlockMatch> matches = new ArrayList<>();


        try {
            JsonObject json = gson.fromJson(new FileReader(jsonPath), JsonObject.class);
            for (String key : json.keySet()) {
                JsonObject block = json.getAsJsonObject(key);

                JsonArray allLabJSON = block.getAsJsonArray("allLab");
                JsonArray topLabJSON = block.getAsJsonArray("topLab");
                JsonArray bottomLabJSON = block.getAsJsonArray("bottomLab");
                JsonArray sideLabJSON = block.getAsJsonArray("sideLab");
                JsonArray endLabJSON = block.getAsJsonArray("endLab");
                double[] allLab = new double[]{
                        allLabJSON.get(0).getAsDouble(),
                        allLabJSON.get(1).getAsDouble(),
                        allLabJSON.get(2).getAsDouble()
                };
                double[] topLab = new double[]{
                        topLabJSON.get(0).getAsDouble(),
                        topLabJSON.get(1).getAsDouble(),
                        topLabJSON.get(2).getAsDouble()
                };
                double[] bottomLab = new double[]{
                        bottomLabJSON.get(0).getAsDouble(),
                        bottomLabJSON.get(1).getAsDouble(),
                        bottomLabJSON.get(2).getAsDouble()
                };
                double[] sideLab = new double[]{
                        sideLabJSON.get(0).getAsDouble(),
                        sideLabJSON.get(1).getAsDouble(),
                        sideLabJSON.get(2).getAsDouble()
                };
                double[] endLab = new double[]{
                        endLabJSON.get(0).getAsDouble(),
                        endLabJSON.get(1).getAsDouble(),
                        endLabJSON.get(2).getAsDouble()
                };
                if (!(allLab[0] == 0 && allLab[1] == 0 && allLab[2] == 0)) {
                    double distance = deltaE2000(lab, allLab);
                    matches.add(new BlockMatch(key, "all", distance));
                }
                if (!(topLab[0] == 0 && topLab[1] == 0 && topLab[2] == 0)) {
                    double distance = deltaE2000(lab, topLab);
                    matches.add(new BlockMatch(key, "top", distance));
                }
                if (!(bottomLab[0] == 0 && bottomLab[1] == 0 && bottomLab[2] == 0)) {
                    double distance = deltaE2000(lab, bottomLab);
                    matches.add(new BlockMatch(key, "bottom", distance));
                }
                if (!(sideLab[0] == 0 && sideLab[1] == 0 && sideLab[2] == 0)) {
                    double distance = deltaE2000(lab, sideLab);
                    matches.add(new BlockMatch(key, "side", distance));
                }
                if (!(endLab[0] == 0 && endLab[1] == 0 && endLab[2] == 0)) {
                    double distance = deltaE2000(lab, endLab);
                    matches.add(new BlockMatch(key, "end", distance));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        matches.sort(Comparator.comparingDouble(m -> m.distance));

        return matches.subList(0, Math.min(amount, matches.size()));
    }
}
