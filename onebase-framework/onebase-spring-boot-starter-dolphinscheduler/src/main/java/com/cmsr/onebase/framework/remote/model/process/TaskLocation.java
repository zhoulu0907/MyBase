package com.cmsr.onebase.framework.remote.model.process;

/**
 * 任务在画布中的位置信息
 */
public class TaskLocation {

    private Long taskCode;
    private int x;
    private int y;

    public Long getTaskCode() { return taskCode; }
    public void setTaskCode(Long taskCode) { this.taskCode = taskCode; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
}

