package com.buildersbliss.model;

import java.util.Map;

public class Block {
    private String id;
    private String parent;
    private Map<String, String> filepath;
    private boolean remove;
    private LabColour lab;

    public void blockEstablish(String id, String parent, Map<String, String> filepath, boolean remove, LabColour lab) {
        this.id = id;
        this.parent = parent;
        this.filepath = filepath;
        this.remove = remove;
        this.lab = lab;
    }

    public class Textures {
        private String top;
        private String bottom;
        private String side;
        private String all;

        public void addTextures(String all, String top, String bottom, String side) {
            this.all = all;
            this.top = top;
            this.bottom = bottom;
            this.side = side;
        }
    }
}
