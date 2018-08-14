package com.gy.sched.client.executor.job.processor;


import com.gy.sched.client.executor.stop.processor.StopJobContext;

/**
 * 停止处理器
 *
 */
public interface StopJobProcessor {

	/**
	 * 停止job
	 * @param context
	 */
	public void process(StopJobContext context);
	
}
