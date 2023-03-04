package com.robin.plan.domain;

import com.robin.plan.domain.enums.TaskRefrenceEnum;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author hehongbing
 * @date 2023/2/27
 */
public class PlanTest {

    /**
     * code dur startDate   parentCode beforeCode       taskType
     * 1    10   2023-04-01  null       null            wbs
     * 2    10  2023-04-01   null       null            wbs
     * 1-1  6   2023-04-01   1          null            task
     * 1-2  4   2023-04-01   1          1-1(1)          task
     * 2-1  5   2023-04-01   2          1-2(0)          task
     * 2-2  5   2023-04-01   2          1-2(0),2-1(0)   task
     */
    public Plan buildPlan() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2023, 3, 1);
        Plan plan = new Plan(calendar.getTime());
        plan.addTask(new Task("1", "1", null, calendar.getTime(), 10));
        plan.addTask(new Task("2", "2", null, calendar.getTime(), 10));
        plan.addTask(new Task("1-1", "1-1", "1", calendar.getTime(), 6));
        plan.addTask(new Task("1-2", "1-2", "1", calendar.getTime(), 4));
        plan.addTask(new Task("2-1", "2-1", "2", calendar.getTime(), 5));
        plan.addTask(new Task("2-2", "2-2", "2", calendar.getTime(), 5));
        // 新增前置任务关系
        plan.addTaskRefrence("1-2", new TaskRefrence("1-1", TaskRefrenceEnum.FS.getRef(), 1));
        plan.addTaskRefrence("2-1", new TaskRefrence("1-2", TaskRefrenceEnum.FS.getRef(), 0));
        plan.addTaskRefrence("2-2", new TaskRefrence("1-2", TaskRefrenceEnum.FS.getRef(), 0));
        plan.addTaskRefrence("2-2", new TaskRefrence("2-1", TaskRefrenceEnum.FS.getRef(), 0));
        return plan;
    }

    @Test
    public void calculate() {
        Plan plan = buildPlan();
        plan.calculate(null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Task task : plan.getTaskMap().values()) {
            System.out.println(task.getTaskCode() + " : " + sdf.format(task.getTargetStartDate()) + " ~ " + sdf.format(task.getTargetEndDate()));
        }
    }

}
