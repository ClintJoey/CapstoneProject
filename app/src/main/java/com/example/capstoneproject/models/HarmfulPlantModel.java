package com.example.capstoneproject.models;

import java.io.Serializable;
import java.util.ArrayList;

public class HarmfulPlantModel implements Serializable {
    private String plantName, plantCommonNames, plantDesc, abundantLocations, plantHarmfulParts;
    private ArrayList<String> plantImages, plantToxins, plantGuides;

    public HarmfulPlantModel(ArrayList<String> plantImages, String plantName, String plantCommonNames, String plantDesc, String abundantLocations,
                             String plantHarmfulParts, ArrayList<String> plantToxins, ArrayList<String> plantGuides) {
        this.plantImages = plantImages;
        this.plantName = plantName;
        this.plantCommonNames = plantCommonNames;
        this.plantDesc = plantDesc;
        this.abundantLocations = abundantLocations;
        this.plantHarmfulParts = plantHarmfulParts;
        this.plantToxins = plantToxins;
        this.plantGuides = plantGuides;
    }
    public HarmfulPlantModel() {

    }

    public ArrayList<String> getPlantImages() {
        return plantImages;
    }

    public void setPlantImages(ArrayList<String> plantImages) {
        this.plantImages = plantImages;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getPlantCommonNames() {
        return plantCommonNames;
    }

    public void setPlantCommonNames(String plantCommonNames) {
        this.plantCommonNames = plantCommonNames;
    }

    public String getPlantDesc() {
        return plantDesc;
    }

    public void setPlantDesc(String plantDesc) {
        this.plantDesc = plantDesc;
    }

    public String getAbundantLocations() {
        return abundantLocations;
    }

    public void setAbundantLocations(String abundantLocations) {
        this.abundantLocations = abundantLocations;
    }

    public String getPlantHarmfulParts() {
        return plantHarmfulParts;
    }

    public void setPlantHarmfulParts(String plantHarmfulParts) {
        this.plantHarmfulParts = plantHarmfulParts;
    }

    public ArrayList<String> getPlantToxins() {
        return plantToxins;
    }

    public void setPlantToxins(ArrayList<String> plantToxins) {
        this.plantToxins = plantToxins;
    }

    public ArrayList<String> getPlantGuides() {
        return plantGuides;
    }

    public void setPlantGuides(ArrayList<String> plantGuides) {
        this.plantGuides = plantGuides;
    }

    @Override
    public String toString() {
        return "HarmfulPlantModel{" +
                "plantPic='" + plantImages + '\'' +
                ", plantName='" + plantName + '\'' +
                ", plantCommonNames='" + plantCommonNames + '\'' +
                ", plantDesc='" + plantDesc + '\'' +
                ", abundantLocations='" + abundantLocations + '\'' +
                ", plantHarmfulParts='" + plantHarmfulParts + '\'' +
                ", plantToxins=" + plantToxins +
                ", plantGuides=" + plantGuides +
                '}';
    }
}
