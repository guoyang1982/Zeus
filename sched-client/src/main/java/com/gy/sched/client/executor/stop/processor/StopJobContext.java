package com.gy.sched.client.executor.stop.processor;


import com.gy.sched.client.executor.job.context.JobContext;
import com.gy.sched.common.domain.store.Job;
import com.gy.sched.common.domain.store.JobInstanceSnapshot;

/**
 * 停止job处理器上下文
 */
public class StopJobContext extends JobContext {

	public StopJobContext(Job job, JobInstanceSnapshot jobInstanceSnapshot, int retryCount) {
		super(job, jobInstanceSnapshot, retryCount);
	}

}
