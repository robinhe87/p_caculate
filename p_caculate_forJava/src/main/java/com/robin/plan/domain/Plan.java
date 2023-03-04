package com.robin.plan.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.robin.plan.domain.enums.TaskRefrenceEnum;
import com.robin.plan.domain.enums.TaskTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author hehongbing
 * @date 2023/2/27
 */
@Slf4j
@Data
public class Plan {

    /**
     * 计划开始时间
     */
    private Date planDate;

    public Plan(Date planDate) {
        this.planDate = planDate;
    }

    public Plan() {
        this.planDate = new Date();
    }

    /**
     * 计划任务
     */
    private List<Task> tasks = new ArrayList<Task>();

    /**
     * 任务缓存
     */
    private Map<String, Task> taskMap = new HashMap<String, Task>();

    /**
     * 添加任务
     */
    public void addTask(Task task) {
        if (task == null) {
            throw new RuntimeException("task不能为空");
        }
        if (taskMap.containsKey(task.getTaskCode())) {
            throw new RuntimeException("该任务编号已经存在!");
        }
        if (StrUtil.isNotBlank(task.getParentCode()) && !taskMap.containsKey(task.getParentCode())) {
            throw new RuntimeException("上级编号不存在!");
        }
        // 校验时间是否合法
        if (validTaskDate(task) == false) {
            throw new RuntimeException("开始时间、工期、结束时间有误，请核验!");
        }

        taskMap.put(task.getTaskCode(), task);

        task.setTaskType(TaskTypeEnum.TASK.getType());

        if (StrUtil.isNotBlank(task.getParentCode())) {
            Task parent = taskMap.get(task.getParentCode());
            parent.setTaskType(TaskTypeEnum.WBS.getType());
            parent.getChildren().add(task);
        } else {
            tasks.add(task);
        }

    }

    private boolean validTaskDate(Task task) {
        if (task.getTargetStartDate() == null) {
            if (task.getDuration() == null || task.getTargetEndDate() == null) {
                return false;
            }
            int duration = task.getDuration() == 0 ? 0 : task.getDuration() - 1;
            task.setTargetStartDate(DateUtil.offsetDay(task.getTargetEndDate(), -1 * duration));
        } else if (task.getDuration() == null) {
            if (task.getTargetStartDate() == null || task.getTargetEndDate() == null) {
                return false;
            }
            task.setDuration((int) DateUtil.between(task.getTargetStartDate(), task.getTargetEndDate(), DateUnit.DAY));
        }

        if (task.getTargetStartDate() == null || task.getDuration() == null) {
            return false;
        }

        int duration = task.getDuration() == 0 ? 0 : task.getDuration() - 1;
        task.setTargetEndDate(DateUtil.offsetDay(task.getTargetStartDate(), 1 * duration));

        return true;
    }

    /**
     * 预留
     */
    public void delTask(String taskId) {
        // todo
    }

    /**
     * 给某个任务增加前置任务
     */
    public void addTaskRefrence(String taskCode, TaskRefrence taskRefrence) {

        if (StrUtil.isBlank(taskCode) || taskRefrence == null) {
            throw new RuntimeException("任务编号和taskRefrence必须传入");
        }
        if (taskMap.containsKey(taskCode) == false) {
            throw new RuntimeException("taskId不正确");
        }
        if (StrUtil.isBlank(taskRefrence.getTaskCode())) {
            throw new RuntimeException("前置任务编号必须传值");
        }
        if (taskMap.containsKey(taskRefrence.getTaskCode()) == false) {
            throw new RuntimeException(String.format("前置任务编号【%s】不存在", taskRefrence.getTaskCode()));
        }

        //当前节点设置前置节点
        Task task = taskMap.get(taskCode);
        task.getBefores().add(taskRefrence);

        //前置节点设置当前节点为其后置节点
        Task beforeTask = taskMap.get(taskRefrence.getTaskCode());
        TaskRefrence afterRefrence = BeanUtil.toBean(taskRefrence, TaskRefrence.class);
        afterRefrence.setTaskCode(taskCode);
        beforeTask.getAfters().add(afterRefrence);
    }

    /**
     * @param startDate 进度计算开始时间
     */
    public void calculate(Date startDate) {

        if (CollUtil.isEmpty(tasks)) {
            return;
        }

        // 记录所有叶子节点
        List<Task> leafs = new ArrayList<Task>();
        for (Task task : taskMap.values()) {
            if (task.getTaskType().equals(TaskTypeEnum.TASK.getType()) && CollUtil.isEmpty(task.getBefores())) {
                // 没有任何前置任务的节点如果不是强制执行，则设置开始时间为进度计算的开始时间
                if (!task.isForceStart() && !task.isForceEnd() && startDate != null) {
                    task.setTargetStartDate(startDate);
                    int duration = task.getDuration() == 0 ? 0 : task.getDuration() - 1;
                    task.setTargetEndDate(DateUtil.offsetDay(task.getTargetStartDate(), duration));
                }
            }
            if (CollUtil.isEmpty(task.getChildren())) {
                leafs.add(task);
            }
        }

        // todo 判断是否有依赖死结，遗留下次处理

        // 进度计算出前置任务
        for (Task task : taskMap.values()) {
            doCalculate(task);
        }

        // 汇总wbs
        for (Task leaf : leafs) {
            calculateWbs(leaf);
        }

    }

    private void doCalculate(Task task) {
        if (CollUtil.isEmpty(task.getBefores())) {
            return;
        }
        Date minStartDate = null;
        Date maxEndDate = null;
        for (TaskRefrence taskRefrence : task.getBefores()) {
            Task beforeTask = taskMap.get(taskRefrence.getTaskCode());
            doCalculate(beforeTask);
            Date start = null;
            Date end = null;
            // todo 先处理FS的前置任务
            if (taskRefrence.getRefType().equals(TaskRefrenceEnum.FS.getRef())) {
                start = DateUtil.offsetDay(beforeTask.getTargetEndDate(), taskRefrence.getDuration()).toJdkDate();
                end = DateUtil.offsetDay(start, task.getDuration() - 1).toJdkDate();
            }
            if (minStartDate == null) {
                minStartDate = start;
                maxEndDate = end;
            } else {
                if (minStartDate.after(start)) {
                    minStartDate = start;
                }
                if (maxEndDate.before(end)) {
                    maxEndDate = end;
                }
            }
        }
        // todo 增加强制约束判断
        task.setTargetStartDate(minStartDate);
        task.setTargetEndDate(maxEndDate);
    }

    private void calculateWbs(Task task) {
        if (task == null) {
            return;
        }
        Date minStartDate = null;
        Date maxEndDate = null;
        for (Task child : task.getChildren()) {
            if (minStartDate == null) {
                minStartDate = child.getTargetStartDate();
                maxEndDate = child.getTargetEndDate();
            }
            if (minStartDate.after(child.getTargetStartDate())) {
                minStartDate = child.getTargetStartDate();
            }
            if (maxEndDate.before(child.getTargetEndDate())) {
                maxEndDate = child.getTargetEndDate();
            }
        }

        if (minStartDate != null) {
            task.setTargetStartDate(minStartDate);
            task.setTargetEndDate(maxEndDate);
        }

        calculateWbs(taskMap.get(task.getParentCode()));

    }

}
