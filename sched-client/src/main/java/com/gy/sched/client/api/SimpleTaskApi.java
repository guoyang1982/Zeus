package com.gy.sched.client.api;

import com.gy.sched.common.domain.store.Job;

/**
 * Created by guoyang on 17/4/10.
 */
public interface SimpleTaskApi {

    /**
     * 简单任务api
     * @param job
     * @return
     */
    public boolean addTask(Job job);
}
