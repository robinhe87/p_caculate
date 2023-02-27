package com.robin.plan.domain.eo;

import lombok.Data;

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
     * 任务ID(唯一)
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 上级任务ID
     */
    private String parentTaskId;

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

    /**
     * 子节点
     */
    private List<Task> children;

    /**
     * 前置节点
     */
    private List<TaskRefrence> befores;

    /**
     * 后置节点
     */
    private List<TaskRefrence> afters;


}
