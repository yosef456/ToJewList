package com.example.ytseitkin.tojewlist;

/**
 * Created by ytseitkin on 10/30/2016.
 */
public class ToDoItem {

    private int day;

    private String text;

    public ToDoItem(int day,String text){

        this.day = day;

        this.text = text;

    }

    public int getDay(){
        return this.day;
    }

    public String getText(){
        return this.text;
    }
}
