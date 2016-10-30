package com.example.ytseitkin.tojewlist;

/**
 * Created by ytseitkin on 10/30/2016.
 */
public class Occasion {

    private int id;

    private String name;

    public Occasion(int id,String name){
        this.id=id;
        this.name=name;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }
}
