package com.buildersbliss.model;


public class LabColour {
    private double l;
    private double a;
    private double b;

    public void LabColour(double l, double a, double b) {
        this.l = l;
        this.a = a;
        this.b = b;
    }

    public double getL() { return l; }
    public double getA() { return a; }
    public double getB() { return b; }
}
