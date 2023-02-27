package com.robin.plan.domain;

import org.junit.Test;

/**
 * @author hehongbing
 * @date 2023/2/27
 */
public class PlanTest {

    private Plan plan = new Plan();

    @Test
    public void testAddTask(){
        plan.addTask(new Task());
    }

}
