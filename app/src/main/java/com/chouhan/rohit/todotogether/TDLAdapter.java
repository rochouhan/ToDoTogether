package com.chouhan.rohit.todotogether;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TDLAdapter extends ArrayAdapter<ToDoList> {

    private final String LOG_TAG = TDLAdapter.class.getSimpleName();
    public TDLAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ToDoList> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View gridView = (View) convertView;

        if (gridView == null){
            gridView = LayoutInflater.from(getContext()).inflate(
                    R.layout.grid_item, parent, false);
        }

        ToDoList toDoList = (ToDoList) getItem(position);
        Log.d(LOG_TAG, toDoList.toString());

        String title = toDoList.getmTitle();
        String creator = toDoList.getmCreatorUsername();
        Log.d(LOG_TAG, "CREATOR " + creator);

        TextView titleView = gridView.findViewById(R.id.TDL_title);
        TextView creatorView = gridView.findViewById(R.id.creator_name_text_view);
        Log.d(LOG_TAG, "creatorView " + creatorView);
        Log.d(LOG_TAG, "titleView " + titleView);

        titleView.setText(title);
        creatorView.setText(creator);

        return gridView;
    }
}
