package com.robin.plan.domain.enums;

public enum TaskTypeEnum {
    /**
     * 工作任务分解
     */
    WBS("WBS"),
    /**
     * 任务项
     */
    TASK("TASK");

    private String type;

    TaskTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
