package com.chouhan.rohit.todotogether;

import java.io.Serializable;

public class Task implements Serializable{
    private String taskName;
    private boolean isCompleted;


    public Task(){}

    public Task(String taskName, boolean isCompleted){
        this.taskName = taskName;
        this.isCompleted = isCompleted;
    }

    public String getTaskName() {
        return taskName;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

}
