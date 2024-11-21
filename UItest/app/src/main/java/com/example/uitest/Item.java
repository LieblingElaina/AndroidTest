package com.example.uitest;

import androidx.annotation.NonNull;

public class Item
{

    private String name;
    private boolean cs;

    public Item(){
        super();
    }

    public Item(String name, boolean cs){
        super();
        this.name = name;
        this.cs = cs;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isCs() {
        return cs;
    }
    public void setCs(boolean cs) {
        this.cs = cs;
    }
    @Override
    public String toString() {
        return "Item{" + "name='" + name + '\'' + ",cs=" + cs + '}';
    }
}