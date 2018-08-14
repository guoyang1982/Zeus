package com.gy.sched.client.executor.job.processor;

import com.gy.sched.common.domain.result.ProcessResult;
import com.gy.sched.client.executor.simple.processor.SimpleJobContext;

/**
 * 简单job处理器*
 */
public interface SimpleJobProcessor {

	/**
	 * 处理简单job的方法
	 */
	public ProcessResult process(SimpleJobContext context);
	
}
