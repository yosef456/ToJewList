package com.example.ytseitkin.tojewlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ytseitkin on 10/30/2016.
 */
public class ToDoAdapter extends ArrayAdapter<ToDoItem> {

    private final Context context;
    private final ArrayList<ToDoItem> values;

    public ToDoAdapter(Context context,  ArrayList<ToDoItem> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.todoitem, parent, false);

        TextView item = (TextView) rowView.findViewById(R.id.item);

        item.setText("-" + values.get(position).getText());

        return rowView;
    }
}
