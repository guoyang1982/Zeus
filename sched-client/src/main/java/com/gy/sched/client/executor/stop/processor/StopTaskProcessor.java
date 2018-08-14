package com.gy.sched.client.executor.stop.processor;

import com.gy.sched.client.context.ClientContext;
import com.gy.sched.common.domain.store.Job;
import com.gy.sched.common.domain.store.JobInstanceSnapshot;
import com.gy.sched.client.executor.job.processor.StopJobProcessor;
import com.gy.sched.common.constants.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 停止任务处理器
 */
public class StopTaskProcessor implements Runnable, ClientContext, Constants {
	
	private static final Log logger = LogFactory.getLog(StopTaskProcessor.class);

	private final Job job;
	
	private final JobInstanceSnapshot jobInstanceSnapshot;
	
	/** 停止任务处理器 */
	private final StopJobProcessor stopJobProcessor;
	
	public StopTaskProcessor(long jobId, long jobInstanceId, StopJobProcessor stopJobProcessor) {
		this.job = new Job();
		this.jobInstanceSnapshot = new JobInstanceSnapshot();
		this.stopJobProcessor = stopJobProcessor;
		
		this.job.setId(jobId);
		this.jobInstanceSnapshot.setId(jobInstanceId);
	}
	
	@Override
	public void run() {

		//停止job处理器上下文
		StopJobContext stopJobContext = new StopJobContext(this.job, this.jobInstanceSnapshot, 0);

		//停止job
		try {
			this.stopJobProcessor.process(stopJobContext);
		} catch (Throwable e) {
			logger.error("[StopTaskProcessor]: process error"
					+ ", job:" + job 
					+ ", jobInstanceSnapshot:" + jobInstanceSnapshot, e);
		}

	}

}
