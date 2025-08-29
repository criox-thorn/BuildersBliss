package com.buildersbliss.core;

import java.util.ArrayList;

public class GradientGenerator {
    public static ArrayList<double[]> gradient(double[] start, double[] end, int steps) {
        ArrayList<double[]> gradient = new ArrayList<>();
        gradient.add(start);

        for (int i = 0; i < steps; i++) {
            double t = (double) i / (steps - 1); // interpolation factor 0 → 1
            double L = start[0] + t * (end[0] - start[0]);
            double a = start[1] + t * (end[1] - start[1]);
            double b = start[2] + t * (end[2] - start[2]);

            gradient.add(new double[]{L,a,b});
        }
        gradient.add(end);
        return gradient;
    }
}
