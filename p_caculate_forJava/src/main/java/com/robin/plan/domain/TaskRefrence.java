package com.robin.plan.domain;

import lombok.Data;

/**
 * @author hehongbing
 * @date 2023/2/27
 * 节点引用关系
 */
@Data
public class TaskRefrence {

    /**
     * 任务编号
     */
    private String taskCode;

    /**
     * 关联类型
     */
    private String refType;

    /**
     * 工期
     */
    private Integer duration;

    public TaskRefrence(String taskCode, String refType, Integer duration) {
        this.taskCode = taskCode;
        this.refType = refType;
        this.duration = duration;
    }
}
