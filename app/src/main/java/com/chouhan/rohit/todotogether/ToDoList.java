package com.chouhan.rohit.todotogether;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ToDoList implements Serializable{
    private String mCreatorUsername;
    private ArrayList<Task> mToDoList;
    private String mTitle;
    private String mUid;
    private String mPassword;

    public ToDoList(){}

    public ToDoList(String title, String creatorUsername, ArrayList<Task> toDoList){
        mCreatorUsername = creatorUsername;
        mToDoList = toDoList;
        mTitle = title;
    }

    public void setmCreatorUsername(String mCreatorUsername) {
        this.mCreatorUsername = mCreatorUsername;
    }

    public void setmToDoList(ArrayList<Task> mToDoList) {
        this.mToDoList = mToDoList;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmCreatorUsername() {
        return mCreatorUsername;
    }

    public ArrayList<Task> getmToDoList() {
        return mToDoList;
    }

    public String getmTitle() {
        return mTitle;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("mCreatorUsername", mCreatorUsername);
        result.put("mToDoList", mToDoList);
        result.put("mTitle", mTitle);
        result.put("mPassword", mPassword);
        return result;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String password){
        mPassword = password;
    }
}
