package io.treasure.task.base;

import lombok.Getter;

public class TaskSock{

    @Getter
    private int taskCounter = 0;

    private boolean taskLock = false;

    public void resetTaskCounter(){
        taskCounter = 0;
    }
    public void updateTaskCounter(){
        taskCounter++;
    }
    public void resetAllTaskCounter(){
        taskCounter = 0;
    }

    public boolean isInProcess(){

        return taskLock;
    }

    public void freeProcessLock(){
        taskLock = false;
    }
    public void lockedProcessLock(){
        taskLock = true;
    }
    public void resetAllTaskLock(){
        taskLock = false;
    }
}