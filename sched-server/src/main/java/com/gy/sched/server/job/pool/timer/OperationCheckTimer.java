package com.gy.sched.server.job.pool.timer;

import com.gy.sched.common.constants.Constants;
import com.gy.sched.server.context.ServerContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.TimerTask;

/**
 * 操作检查定时器
 * @author tianyao.myc
 *
 */
public class OperationCheckTimer extends TimerTask implements ServerContext, Constants {

	private static final Log logger = LogFactory.getLog(OperationCheckTimer.class);
	
	@Override
	public void run() {
		try {
			/** 处理各项操作 */
			//jobPool.handleOperations();
		} catch (Throwable e) {
			logger.error("[OperationCheckTimer]: handleOperations error", e);
		}
	}

}
