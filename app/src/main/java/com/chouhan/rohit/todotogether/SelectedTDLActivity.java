package com.chouhan.rohit.todotogether;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class SelectedTDLActivity extends AppCompatActivity {
    private static final String LOG_TAG = SelectedTDLActivity.class.getSimpleName();
    private ToDoList mToDoList;
    public static ArrayList<Task> mList;
    private String creatorUsername;
    private String title;
    private String uid;
    private String password;

    private TaskAdapter mTaskAdapter;
    private FloatingActionButton mFloatingActionButton;
    private static EditText mEditText;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReferenceUsers;
    private DatabaseReference mDatabaseReferenceLists;
    private FirebaseUser mFirebaseUser;


    private static int mTaskPosition = -1;
    private static int mListPosition = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_tdl);

        mFirebaseDatabase = MainActivity.mFirebaseDatabase;
        mDatabaseReferenceUsers = MainActivity.mDatabaseReferenceUsers;
        mDatabaseReferenceLists = MainActivity.mDatabaseReferenceLists;
        mFirebaseUser = MainActivity.mFirebaseUser;
        Intent intent = getIntent();

        mToDoList =  (ToDoList) intent.getSerializableExtra(MainActivity.EXTRA_TO_DO_LIST_OBJ);

        if (mToDoList == null){
//            Log.d(LOG_TAG, "MTODOLIST is null");
            mToDoList = new ToDoList();
            mList = new ArrayList<>();
            setTitle("Add a new list");
            creatorUsername = mFirebaseUser.getDisplayName();
        }

        else{

            mList = mToDoList.getmToDoList();
            title = mToDoList.getmTitle();
            uid = mToDoList.getmUid();
            creatorUsername = mToDoList.getmCreatorUsername();
            password = mToDoList.getmPassword();
            setTitle("Edit " + title);
        }
        if(mList == null){
            mList = new ArrayList<>();
        }

        if(password == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please Create Password");

// Set up the input
            final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    password = input.getText().toString();
                    mToDoList.setmPassword(password);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }


        if(title == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Title");

// Set up the input
            final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    title = input.getText().toString();
                    mToDoList.setmTitle(title);
                    setTitle(title);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }



        mEditText = findViewById(R.id.add_task_edit_text);

        mTaskAdapter = new TaskAdapter(getApplicationContext(), 0, mList);

        final ListView listView = findViewById(R.id.selected_tdl_list_view);

        listView.setAdapter(mTaskAdapter);



        mFloatingActionButton = findViewById(R.id.fab_add_task);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToList();
                mEditText.setText("");
                mEditText.clearFocus();
            }
        });


    }

    public void addToList(){
        String taskString = mEditText.getText().toString();
        Task task = new Task(taskString, false);

        if (mTaskPosition != -1) {
            Log.v(LOG_TAG, "MList Check 1" + mList.size());

            mList.set(mTaskPosition, task);

            Log.v(LOG_TAG, "MList Check 2" + mList.size());
        }
        else {
            mList.add(task);

        }

        mTaskPosition = -1;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tdl_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            saveToDoList();
            finish();
            return true;
        }
        else if (id == R.id.action_delete){
            deleteToDoList();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveToDoList(){
        ToDoList toDoList = mToDoList;
        toDoList.setmToDoList(mList);
        toDoList.setmCreatorUsername(creatorUsername);
        toDoList.setmTitle(title);

        if (getIntent().getExtras() == null) {
            String uniqueKey = mDatabaseReferenceLists.push().getKey();
            toDoList.setmUid(uniqueKey);
            mDatabaseReferenceLists.child(uniqueKey).setValue(toDoList);
            mDatabaseReferenceUsers.child(MainActivity.FIREBASE_USER_CHILD).child(uniqueKey).setValue(toDoList);
//            MainActivity.mTDLAdapter.add(mToDoList);

        }
        else{
            Map<String, Object> update_values = toDoList.toMap();
            mDatabaseReferenceLists.child(uid).updateChildren(update_values);
            mDatabaseReferenceUsers.child(MainActivity.FIREBASE_USER_CHILD).child(uid).setValue(toDoList);
        }

    }

    public void deleteToDoList(){
        mDatabaseReferenceLists.child(uid).removeValue();
        mDatabaseReferenceUsers.child(MainActivity.FIREBASE_USER_CHILD).child(uid).removeValue();
    }

    public static void editTask(Task taskPrev, int position){
        mEditText.setText(taskPrev.getTaskName());
        mTaskPosition = position;
    }
}
