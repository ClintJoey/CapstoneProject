package com.example.capstoneproject.models;

public class AddedReportModel {
    private String name;
    private int count;

    public AddedReportModel(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int increaseCount() {
        return this.count += 1;
    }

    public int decreaseCount() {
        return this.count -= 1;
    }
}
