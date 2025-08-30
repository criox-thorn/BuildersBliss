package com.buildersbliss.core;

import com.buildersbliss.io.BlockMatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.buildersbliss.io.BlockMatch.match;

public class GradientGenerator {
    public static ArrayList<double[]> gradient(double[] start, double[] end, int steps) {
        ArrayList<double[]> gradient = new ArrayList<>();

        for (int i = 0; i < steps; i++) {
            double t = (double) i / (steps - 1); // interpolation factor 0 → 1
            double L = start[0] + t * (end[0] - start[0]);
            double a = start[1] + t * (end[1] - start[1]);
            double b = start[2] + t * (end[2] - start[2]);

            gradient.add(new double[]{L,a,b});
        }
        return gradient;
    }

    public static ArrayList<List<BlockMatch>> blockMatchingGradient(double[] lab1, double[] lab2, int steps, int blockMatches, String blockJSONPath) {
        ArrayList<double[]> gradient = gradient(lab1, lab2, steps);

        ArrayList<List<BlockMatch>> mainArray = new ArrayList<>();

        for (double[] arr : gradient) {
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
