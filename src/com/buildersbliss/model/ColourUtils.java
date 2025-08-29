package com.buildersbliss.model;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class ColourUtils {
    private static final double X_REF = 95.047;
    private static final double Y_REF = 100.0;
    private static final double Z_REF = 108.883;

    public static Color colourAverager(File file) {
        int avgRed = 0;
        int avgGreen = 0;
        int avgBlue = 0;
        try{
            BufferedImage originalImage = ImageIO.read(file);
            int imageWidth = originalImage.getWidth();
            int imageHeight = originalImage.getHeight();
            long sumRed = 0;
            long sumGreen = 0;
            long sumBlue = 0;

            // Get Colour values
            for (int y = 0; y < imageHeight; y++) {
                for (int x = 0; x < imageWidth; x++) {
                    int pixel = originalImage.getRGB(x, y); // Get the RGB integer value
                    Color colour = new Color(pixel, true); // Create a Color object

                    sumRed += colour.getRed();
                    sumGreen += colour.getGreen();
                    sumBlue += colour.getBlue();
                }
            }

            // Average Values
            int numPixels = imageWidth * imageHeight;
            avgRed = (int) (sumRed / numPixels);
            avgGreen = (int) (sumGreen / numPixels);
            avgBlue = (int) (sumBlue / numPixels);
        }
        catch (Exception e) {
            System.out.println("Cannot Load Image File");
        }
        return new Color(avgRed, avgGreen, avgBlue);
    }
    public static double[] rgbToLab(Color colour) {
        double r = colour.getRed() / 255.0;
        double bl = colour.getBlue() / 255.0;
        double g = colour.getGreen() / 255.0;

        r = (r > 0.04045) ? Math.pow((r + 0.055) / 1.055, 2.4) : r / 12.92;
        g = (g > 0.04045) ? Math.pow((g + 0.055) / 1.055, 2.4) : g / 12.92;
        bl = (bl > 0.04045) ? Math.pow((bl + 0.055) / 1.055, 2.4) : bl / 12.92;

        double X = (0.4124 * r + 0.3576 * g + 0.1805 * bl) * 100;
        double Y = (0.2126 * r + 0.7152 * g + 0.0722 * bl) * 100;
        double Z = (0.0193 * r + 0.1192 * g + 0.9505 * bl) * 100;

        double fx = funky(X / X_REF);
        double fy = funky(Y / Y_REF);
        double fz = funky(Z / Z_REF);

        double L = 116 * fy - 16;
        double a = 500 * (fx - fy);
        double b = 200 * (fy - fz);

        return new double[]{L,a,b};
    }

    private static double funky(double val) {
        if (val > 0.008856) {
            return Math.pow(val, 1.0 / 3.0);
        } else {
            return (7.787 * val) + (16.0 / 116.0);
        }
    }

    public static double deltaE2000(double[] lab1, double[] lab2) {
        double L1 = lab1[0], a1 = lab1[1], b1 = lab1[2];
        double L2 = lab2[0], a2 = lab2[1], b2 = lab2[2];

        double avgLp = (L1 + L2) / 2.0;
        double C1 = Math.sqrt(a1 * a1 + b1 * b1);
        double C2 = Math.sqrt(a2 * a2 + b2 * b2);
        double avgC = (C1 + C2) / 2.0;

        double G = 0.5 * (1 - Math.sqrt(Math.pow(avgC, 7) / (Math.pow(avgC, 7) + Math.pow(25.0, 7))));
        double a1p = (1 + G) * a1;
        double a2p = (1 + G) * a2;
        double C1p = Math.sqrt(a1p * a1p + b1 * b1);
        double C2p = Math.sqrt(a2p * a2p + b2 * b2);
        double avgCp = (C1p + C2p) / 2.0;

        double h1p = Math.atan2(b1, a1p);
        if (h1p < 0) h1p += 2 * Math.PI;
        double h2p = Math.atan2(b2, a2p);
        if (h2p < 0) h2p += 2 * Math.PI;

        double avgHp;
        if (Math.abs(h1p - h2p) > Math.PI) {
            avgHp = (h1p + h2p + 2 * Math.PI) / 2.0;
        } else {
            avgHp = (h1p + h2p) / 2.0;
        }

        double T = 1 - 0.17 * Math.cos(avgHp - Math.toRadians(30))
                + 0.24 * Math.cos(2 * avgHp)
                + 0.32 * Math.cos(3 * avgHp + Math.toRadians(6))
                - 0.20 * Math.cos(4 * avgHp - Math.toRadians(63));

        double dLp = L2 - L1;
        double dCp = C2p - C1p;

        double dhp;
        if (Math.abs(h2p - h1p) <= Math.PI) {
            dhp = h2p - h1p;
        } else if (h2p <= h1p) {
            dhp = h2p - h1p + 2 * Math.PI;
        } else {
            dhp = h2p - h1p - 2 * Math.PI;
        }

        double dHp = 2 * Math.sqrt(C1p * C2p) * Math.sin(dhp / 2.0);

        double SL = 1 + ((0.015 * Math.pow(avgLp - 50, 2)) /
                Math.sqrt(20 + Math.pow(avgLp - 50, 2.0)));
        double SC = 1 + 0.045 * avgCp;
        double SH = 1 + 0.015 * avgCp * T;

        double dRo = 30 * Math.exp(-Math.pow((avgHp - Math.toRadians(275)) / Math.toRadians(25), 2));
        double RC = 2 * Math.sqrt(Math.pow(avgCp, 7) / (Math.pow(avgCp, 7) + Math.pow(25.0, 7)));
        double RT = -RC * Math.sin(2 * Math.toRadians(dRo));

        double dE = Math.sqrt(Math.pow(dLp / SL, 2) +
                Math.pow(dCp / SC, 2) +
                Math.pow(dHp / SH, 2) +
                RT * (dCp / SC) * (dHp / SH));

        return dE;
    }

    public static double[] randomColourGenerator() {
        Random random = new Random();

        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        Color rgbColour = new Color(red, green, blue);

        return rgbToLab(rgbColour);
    }
}
