package com.gy.sched.server.zookeeper.timer;

import com.gy.sched.common.constants.Constants;
import com.gy.sched.common.zk.ZkManager;
import com.gy.sched.server.context.ServerContext;
import com.gy.sched.common.util.PathUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.TimerTask;

/**
 * ZK节点检查定时器
 */
public class ZkCheckTimer extends TimerTask implements ServerContext, Constants {

	private static final Log logger = LogFactory.getLog(ZkCheckTimer.class);
	
	@Override
	public void run() {
		
		try {
			
			//检查Server
			checkServer();
			
			//刷新服务器缓存列表
			refreshServerListCache();
		} catch (Throwable e) {
			logger.error("[ZkCheckTimer]: run error, serverConfig:" + serverConfig, e);
		}
		
	}
	
	/**
	 * 检查Server
	 */
	private void checkServer() {
		
		String serverPath = PathUtil.getServerPath(serverConfig.getLocalAddress());
		
		ZkManager zkManager = zookeeper.getZkManager();
		
		String data = null;
		try {
			data = zkManager.getData(serverPath);
		} catch (Throwable e) {
			logger.error("[ZkCheckTimer]: getData error, serverPath:" + serverPath, e);
		}
		
		if(serverConfig.getDescription().equals(data)) {
			return ;
		}
		
		logger.warn("[ZkCheckTimer]: data disappear, serverPath:" + serverPath);
		
		try {
			zkManager.publishOrUpdateData(serverPath, serverConfig.getDescription(), false);
		} catch (Throwable e) {
			logger.error("[ZkCheckTimer]: publishOrUpdateData error, serverPath:" + serverPath, e);
		}
	}

	/**
	 * 刷新服务器缓存列表
	 */
	private void refreshServerListCache() {

		List<String> serverList = zookeeper.getServerList();
		
		if(CollectionUtils.isEmpty(serverList)) {
			logger.warn("[ZkCheckTimer]: serverList is empty, serverConfig:" + serverConfig.toString());
			return ;
		}

		/** 更新服务端地址列表缓存 */
		//clientRemoting.setServerListCache(serverList);
		
	}
	
}
