package com.website.alphafitness;

/**
 * Created by jayashreemadhanraj on 10/20/16.
 */

public class User {

    int id;
    double weight;
    double height;
    String name;

    public User(){

    }

    public User(String name, double weight, double height) {
        this.name = name;
        this.weight = weight;
        this.height = height;
    }

    public User(double weight, double height, String name) {
        this.height = height;
        this.weight = weight;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setName(String name) {
        this.name = name;
    }


}
