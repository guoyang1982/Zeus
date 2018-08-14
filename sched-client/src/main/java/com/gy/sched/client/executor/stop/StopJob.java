package com.gy.sched.client.executor.stop;

import com.gy.sched.client.context.ClientContext;
import com.gy.sched.client.executor.job.processor.StopJobProcessor;
import com.gy.sched.client.executor.stop.processor.StopTaskProcessor;
import com.gy.sched.common.constants.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.*;

/**
 * 终止job
 *
 */
public class StopJob implements ClientContext, Constants {
	
	private static final Log logger = LogFactory.getLog(StopJob.class);

	/** 指令队列 */
	private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
	
	/** 消息消费线程池 */
	private ThreadPoolExecutor executor = null;
	
	public StopJob() {
		
		this.executor = new ThreadPoolExecutor(0, 30, 
				30 * 1000L, TimeUnit.MILLISECONDS, this.queue, new ThreadFactory(){

			int index = 0;
			
			public Thread newThread(Runnable runnable) {
				index ++;
				return new Thread(runnable, "DTS-StopJobProcessor-" + index);
			}
			
		});
		
	}
	
	/**
	 * 停止任务
	 * @param jobId
	 * @param jobInstanceId
	 */
	public void stopTask(long jobId, long jobInstanceId) {
		
		StopJobProcessor stopJobProcessor = clientConfig.getStopJobProcessor();
		if(null == stopJobProcessor) {
			return ;
		}
		
		try {
			this.executor.execute(new StopTaskProcessor(jobId, jobInstanceId, stopJobProcessor));
		} catch (Throwable e) {
			logger.error("[StopJob]: execute error"
					+ ", jobId:" + jobId 
					+ ", jobInstanceId:" + jobInstanceId, e);
		}
		
	}
	
}
