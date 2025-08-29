package com.buildersbliss.model;

import java.util.Map;

public class Block {
    private String parent;
    private String filepath;
    private String[] textures;
    private double[] allLab;
    private double[] topLab;
    private double[] bottomLab;
    private double[] sideLab;
    private double[] endLab;


    public Block(String parent, String filepath, String[] textures, double[] allLab, double[] topLab, double[] bottomLab, double[] sideLab, double[] endLab) {
        this.parent = parent;
        this.filepath = filepath;
        this.textures = textures;
        this.allLab = allLab;
        this.topLab = topLab;
        this.bottomLab = bottomLab;
        this.sideLab = sideLab;
        this.endLab = endLab;

    }
}
