package com.example.ytseitkin.tojewlist;

/**
 * Created by ytseitkin on 10/30/2016.
 */
public class ToDoItem {

    private String details;
    private String list_type;
    private int master_id;
    private int list_id;

    public ToDoItem(String details,int master_id,int list_id,String list_type){

        this.details = details;
        this.master_id=master_id;
        this.list_id=list_id;
        this.list_type=list_type;

    }

    public String getDetails(){
        return this.details;
    }

    public String getList_type(){
        return this.list_type;
    }

    public int getList_id(){
        return this.list_id;
    }

    public int getMaster_id(){
        return this.master_id;
    }
}
