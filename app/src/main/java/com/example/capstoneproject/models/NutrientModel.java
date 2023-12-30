package com.example.capstoneproject.models;

import java.io.Serializable;

public class NutrientModel implements Serializable {
    private String nutrientName, nutrientDesc, nutrientBenefits;

    public NutrientModel(String nutrientName, String nutrientDesc, String nutrientBenefits) {
        this.nutrientName = nutrientName;
        this.nutrientDesc = nutrientDesc;
        this.nutrientBenefits = nutrientBenefits;
    }

    public NutrientModel() {
    }

    public String getNutrientName() {
        return nutrientName;
    }

    public void setNutrientName(String nutrientName) {
        this.nutrientName = nutrientName;
    }

    public String getNutrientDesc() {
        return nutrientDesc;
    }

    public void setNutrientDesc(String nutrientDesc) {
        this.nutrientDesc = nutrientDesc;
    }

    public String getNutrientBenefits() {
        return nutrientBenefits;
    }

    public void setNutrientBenefits(String nutrientBenefits) {
        this.nutrientBenefits = nutrientBenefits;
    }

    @Override
    public String toString() {
        return "NutrientModel{" +
                "nutrientName='" + nutrientName + '\'' +
                ", nutrientDesc='" + nutrientDesc + '\'' +
                ", nutrientBenefits='" + nutrientBenefits + '\'' +
                '}';
    }
}
