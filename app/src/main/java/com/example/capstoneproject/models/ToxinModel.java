package com.example.capstoneproject.models;

import java.io.Serializable;

public class ToxinModel implements Serializable {
    private String toxinName, toxinDesc, toxinConsumptionLevel, toxinEffects;

    public ToxinModel(String toxinName, String toxinDesc, String toxinConsumptionLevel, String toxinEffects) {
        this.toxinName = toxinName;
        this.toxinDesc = toxinDesc;
        this.toxinConsumptionLevel = toxinConsumptionLevel;
        this.toxinEffects = toxinEffects;
    }

    public ToxinModel() {
    }

    public String getToxinName() {
        return toxinName;
    }

    public void setToxinName(String toxinName) {
        this.toxinName = toxinName;
    }

    public String getToxinDesc() {
        return toxinDesc;
    }

    public void setToxinDesc(String toxinDesc) {
        this.toxinDesc = toxinDesc;
    }

    public String getToxinConsumptionLevel() {
        return toxinConsumptionLevel;
    }

    public void setToxinConsumptionLevel(String toxinLevel) {
        this.toxinConsumptionLevel = toxinLevel;
    }

    public String getToxinEffects() {
        return toxinEffects;
    }

    public void setToxinEffects(String toxinEffects) {
        this.toxinEffects = toxinEffects;
    }

    @Override
    public String toString() {
        return "ToxinModel{" +
                "toxinName='" + toxinName + '\'' +
                ", toxinDesc='" + toxinDesc + '\'' +
                ", toxinLevel='" + toxinConsumptionLevel + '\'' +
                ", toxinEffects='" + toxinEffects + '\'' +
                '}';
    }
}
