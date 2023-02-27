package com.robin.plan.domain.enums;

public enum TaskRefrenceEnum {
    /**
     * 完成-开始
     */
    FS("F-S"),
    /**
     * 完成-完成
     */
    FF("F-F");

    private String ref;

    public String getRef() {
        return ref;
    }

    TaskRefrenceEnum(String ref) {
        this.ref = ref;
    }
}
