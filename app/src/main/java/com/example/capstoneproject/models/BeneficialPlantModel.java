package com.example.capstoneproject.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class BeneficialPlantModel implements Serializable {
    private ArrayList<String> plantImages;
    private String plantName, plantCommonNames, plantDesc, plantAbundantLocations;
    private ArrayList<String> plantNutrients, plantNutrientsAmount;

    public BeneficialPlantModel(ArrayList<String> plantImages, String plantName, String plantCommonNames, String plantDesc, String plantAbundantLocations, ArrayList<String> plantNutrients, ArrayList<String> plantNutrientsAmount) {
        this.plantImages = plantImages;
        this.plantName = plantName;
        this.plantCommonNames = plantCommonNames;
        this.plantDesc = plantDesc;
        this.plantAbundantLocations = plantAbundantLocations;
        this.plantNutrients = plantNutrients;
        this.plantNutrientsAmount = plantNutrientsAmount;
    }
    public BeneficialPlantModel() {

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

    public String getPlantAbundantLocations() {
        return plantAbundantLocations;
    }

    public void setPlantAbundantLocations(String plantAbundantLocations) {
        this.plantAbundantLocations = plantAbundantLocations;
    }

    public ArrayList<String> getPlantNutrients() {
        return plantNutrients;
    }

    public void setPlantNutrients(ArrayList<String> plantNutrients) {
        this.plantNutrients = plantNutrients;
    }

    public ArrayList<String> getPlantNutrientsAmount() {
        return plantNutrientsAmount;
    }

    public void setPlantNutrientsAmount(ArrayList<String> plantNutrientsAmount) {
        this.plantNutrientsAmount = plantNutrientsAmount;
    }

    @Override
    public String toString() {
        return "BeneficialPlantModel{" +
                "plantImages=" + plantImages +
                ", plantName='" + plantName + '\'' +
                ", plantCommonNames='" + plantCommonNames + '\'' +
                ", plantDesc='" + plantDesc + '\'' +
                ", plantAbundantLocations='" + plantAbundantLocations + '\'' +
                ", plantNutrients=" + plantNutrients +
                ", plantNutrientsAmount=" + plantNutrientsAmount +
                '}';
    }
}
