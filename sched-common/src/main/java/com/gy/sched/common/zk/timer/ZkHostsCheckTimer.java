package com.gy.sched.common.zk.timer;

import com.gy.sched.common.zk.ZkManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.TimerTask;

/**
 * ZkHosts检查定时器
 *
 */
public class ZkHostsCheckTimer extends TimerTask {
	
	private static final Log log = LogFactory.getLog(ZkHostsCheckTimer.class);

	private final ZkManager zkManager;
	
	public ZkHostsCheckTimer(ZkManager zkManager) {
		this.zkManager = zkManager;
	}
	
	@Override
	public void run() {
		try {
			zkManager.initZkClient();
		} catch (Throwable e) {
			log.error("[ZkHostsCheckTimer]: initZkClient error", e);
		}

	}

}
