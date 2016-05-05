package com.city.support.sys.update;

/**
 * Created by wys on 2016/5/3.
 */
public enum TaskState {
    READY(1), EXECUTE(2), FINISH(3),WAIT(4),RELEASE(5);
    private Integer type;

    private TaskState(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public TaskState next() {
        switch (this.type) {
            case 1:
                return TaskState.EXECUTE;
            case 2:
                return TaskState.FINISH;
            case 3:
                return TaskState.FINISH;
            case 4:
                return TaskState.READY;
            case 5:
                return TaskState.FINISH;
            default:
                return TaskState.READY;
        }
    }

}
