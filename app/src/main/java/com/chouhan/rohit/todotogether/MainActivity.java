package com.chouhan.rohit.todotogether;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private String mUsername;
    private ArrayList<ToDoList> mToDoLists;

    public static final String DATABASE_CHILD_USERS = "users";
    public static final String DATABASE_CHILD_LISTS = "lists";

    public static final int RC_SIGN_IN = 1;

    public static FirebaseUser mFirebaseUser;
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReferenceUsers;
    public static DatabaseReference mDatabaseReferenceLists;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;

    public static ChildEventListener mListsChildEventsListener;
    public static ChildEventListener mUserseChildEventsListener;

    private ArrayList<Task> test_array = new ArrayList<>();

    private ToDoList TEST_TDL;

    public static TDLAdapter mTDLAdapter;

    public static final String EXTRA_TO_DO_LIST_OBJ = "ToDoListObj";

    public static final String EXTRA_TDL = "ToDoList";

    public static final String EXTRA_TDL_TITLE = "TDLTitle";

    public static final String EXTRA_TDL_CREATOR = "creator";

    public static final String EXTRA_TDL_UID = "uid";

    public static final String EXTRA_TDL_POSITION = "position";

    public static final String FIREBASE_USER_CHILD = "To Do Lists";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        test_array.add(new Task("task1", true));
        test_array.add(new Task("task2", false));
        test_array.add(new Task("task3", false));


        TEST_TDL = new ToDoList("Test", "rocho", test_array);


        Log.d(LOG_TAG, "creator username " + TEST_TDL.getmCreatorUsername());

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mDatabaseReferenceLists = mFirebaseDatabase.getReference().child(DATABASE_CHILD_LISTS);
        mFirebaseAuth = FirebaseAuth.getInstance();

//        String uniqueKey = mDatabaseReferenceLists.push().getKey();
//        TEST_TDL.setmUid(uniqueKey);
//        mDatabaseReferenceLists.child(uniqueKey).setValue(TEST_TDL);

        final GridView gridView = (GridView) findViewById(R.id.gridview);

        mToDoLists = new ArrayList<>();
//        mToDoLists.add(TEST_TDL);


        mTDLAdapter = new TDLAdapter(this, 0, mToDoLists);
        gridView.setAdapter(mTDLAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent selectedTDLActivityIntent = new Intent(MainActivity.this, SelectedTDLActivity.class);
                ToDoList toDoList = (ToDoList) gridView.getItemAtPosition(position);
                selectedTDLActivityIntent.putExtra(EXTRA_TO_DO_LIST_OBJ, toDoList);
                selectedTDLActivityIntent.putExtra(EXTRA_TDL_POSITION, position);
                startActivity(selectedTDLActivityIntent);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent selectedTDLActivityIntent = new Intent(MainActivity.this, SelectedTDLActivity.class);
                startActivity(selectedTDLActivityIntent);
            }
        });

        setUpAuthListener();

//        setUpListsListener();




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            AuthUI.getInstance().signOut(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public int findTDLPosition(String key) {
        int result = -1;
        for (ToDoList tdl : mToDoLists) {
            if (tdl.getmUid() == key) {
                return mToDoLists.indexOf(tdl);
            }
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

//    public void setUpListsListener(){
//        mListsChildEventsListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                ToDoList toDoList = dataSnapshot.getValue(ToDoList.class);
//                mTDLAdapter.add(toDoList);
////                mTDLAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String prevChildKey) {
//                ToDoList toDoList = dataSnapshot.getValue(ToDoList.class);
//                int position = findTDLPosition(toDoList.getmUid());
//                mToDoLists.set(position, toDoList);
////                mTDLAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                ToDoList toDoListFound = dataSnapshot.getValue(ToDoList.class);
//                int position = findTDLPosition(toDoListFound.getmUid());
//                Log.v(LOG_TAG, "mToDoLists pre size " + mToDoLists.size());
//                mTDLAdapter.remove(mToDoLists.get(position));
//                Log.v(LOG_TAG, "mToDoLists post size " + mToDoLists.size());
////                mTDLAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//        mDatabaseReferenceLists.addChildEventListener(mListsChildEventsListener);

//    }

    public void setUpAuthListener(){
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Toast.makeText(MainActivity.this, "You're now signed in. Welcome to ToDoTogether.", Toast.LENGTH_SHORT).show();
                    mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    mDatabaseReferenceUsers = mFirebaseDatabase.getReference().child(DATABASE_CHILD_USERS).child(mFirebaseUser.getUid());
                    mTDLAdapter.clear();
                    setUpUsersListener();
                } else {
                    // User is signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    public void setUpUsersListener(){
        mUserseChildEventsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ToDoList toDoList = dataSnapshot.getValue(ToDoList.class);
                mTDLAdapter.add(toDoList);
//                mTDLAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String prevChildKey) {
                ToDoList toDoList = dataSnapshot.getValue(ToDoList.class);
                int position = findTDLPosition(toDoList.getmUid());
                mToDoLists.set(position, toDoList);
//                mTDLAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                ToDoList toDoListFound = dataSnapshot.getValue(ToDoList.class);
                int position = findTDLPosition(toDoListFound.getmUid());
                Log.v(LOG_TAG, "mToDoLists pre size " + mToDoLists.size());
                mTDLAdapter.remove(mToDoLists.get(position));
                Log.v(LOG_TAG, "mToDoLists post size " + mToDoLists.size());
//                mTDLAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReferenceUsers.child(FIREBASE_USER_CHILD).addChildEventListener(mUserseChildEventsListener);
    }
}
