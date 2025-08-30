package com.buildersbliss.core;
import com.buildersbliss.io.BlockMatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.buildersbliss.io.BlockMatch.match;
import static com.buildersbliss.model.ColourUtils.*;

public class PaletteGenerator {
    public static ArrayList<double[]> generatePaletteFromColour(double [] lab, int amount) {
        ArrayList<double[]> palette = new ArrayList<>();
        double[] lch = labToLch(lab);

        double step = 30;
        for (int i = 0; i < amount; i++) {
            double[] newLCH = new double[]{lch[0],lch[1],(lch[2] + i * step) % 360};

            palette.add(lchToLab(newLCH));
        }
        return palette;
    }

    public static ArrayList<double[]> generateRandomPalette(int amount) {
        double[] rootColour = randomColourGenerator();
        return generatePaletteFromColour(rootColour, amount);
    }

    public static ArrayList<List<BlockMatch>> blockMatchingPalette(int amount, int blockMatches, String blockJSONPath) {
        ArrayList<double[]> palette = generateRandomPalette(amount);

        ArrayList<List<BlockMatch>> mainArray = new ArrayList<>();

        for (double[] arr : palette) {
            System.out.println(Arrays.toString(arr));
            List<BlockMatch> colourMatch = match(arr, blockJSONPath, blockMatches);

            for (BlockMatch block : colourMatch) {
                System.out.println(block.blockId + " | " + block.face);
            }
            mainArray.add(colourMatch);
        }

        return mainArray;
    }
}
