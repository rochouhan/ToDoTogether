package com.chouhan.rohit.todotogether;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class TaskAdapter extends ArrayAdapter<Task> {
    public TaskAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Task> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = (View) convertView;

        if (listView == null){
            listView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        final Task task = getItem(position);

        CheckBox checkBox = listView.findViewById(R.id.task_check_box);
        checkBox.setText(task.getTaskName());
        checkBox.setChecked(task.isCompleted());
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task.isCompleted()){
                    task.setCompleted(false);
                }
                else{
                    task.setCompleted(true);
                }
                SelectedTDLActivity.mList.set(position, task);
            }
        });

        ImageView imageView = listView.findViewById(R.id.edit_task_image_view);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               SelectedTDLActivity.editTask(task, position);
            }
        });

        return listView;
    }


}
