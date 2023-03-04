package com.robin.plan.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author hehongbing
 * @date 2023/2/27
 * 任务节点
 */
@Data
public class Task {

    /**
     * 任务编号(唯一)
     */
    private String taskCode;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 上级任务编号
     */
    private String parentCode;

    /**
     * 计划开始时间
     */
    private Date targetStartDate;

    /**
     * 强制开始
     */
    private boolean forceStart;

    /**
     * 计划结束时间
     */
    private Date targetEndDate;

    /**
     * 强制结束
     */
    private boolean forceEnd;

    /**
     * 工期
     */
    private Integer duration;

    public Task(String taskCode, String taskName, String parentCode, Date targetStartDate, Integer duration) {
        this.taskCode = taskCode;
        this.taskName = taskName;
        this.parentCode = parentCode;
        this.targetStartDate = targetStartDate;
        this.duration = duration;
    }

    /**
     * 子节点
     */
    private List<Task> children = new ArrayList<Task>();

    /**
     * 前置节点
     */
    private List<TaskRefrence> befores = new ArrayList<TaskRefrence>();

    /**
     * 后置节点
     */
    private List<TaskRefrence> afters = new ArrayList<TaskRefrence>();

}
