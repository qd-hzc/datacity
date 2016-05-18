package com.city.support.sys.update;

import java.lang.reflect.Method;

/**
 * Created by wys on 2016/5/3.
 *
 * 任务命令
 */
public class EsiAction implements Action {

    private EsiRule esiRule;

    private String command;//取报表 command = "getRpt"

    public EsiAction(EsiRule esiRule, String command) {
        this.esiRule = esiRule;
        this.command = command;
    }

    @Override
    public void execute(Task task) {
        if (esiRule != null) {

            Class clazz = esiRule.getClass();
            try {
                Method method = clazz.getMethod(command, Task.class);
                method.invoke(esiRule, task);
            } catch (Exception e) {
                e.printStackTrace();
                //执行任务失败记录日志task可以获取数据、环境、命令内容
            }
        }
    }
}
