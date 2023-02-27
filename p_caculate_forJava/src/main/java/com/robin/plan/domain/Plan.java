package com.robin.plan.domain;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author hehongbing
 * @date 2023/2/27
 */
@Slf4j
public class Plan {

    private List<Task> tasks;

    public void addTask(Task task) {
        // todo
        log.info("task:" + task);
    }

    public void caculate() {
        // todo
    }

}
