package com.gy.sched.client.remoting.timer;

import com.gy.sched.client.context.ClientContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.TimerTask;

/**
 * 心跳定时器
 *
 */
public class ClientHeartBeatTimer extends TimerTask implements ClientContext {

	private static final Log logger = LogFactory.getLog(ClientHeartBeatTimer.class);
	
	@Override
	public void run() {
		try {

			List<String> serverList = zookeeper.getServerList();

			if(CollectionUtils.isEmpty(serverList)) {
				logger.warn("[ClientHeartBeatTimer]: serverList is empty, clientConfig:" + clientConfig.toString());
				return ;
			}

			/** 更新服务端地址列表缓存 */
			clientRemoting.setServerListCache(serverList);

			for(String server : serverList) {

				try {
					clientRemoting.connectServer(server);
				} catch (Throwable e) {
					logger.error("[ClientHeartBeatTimer]: connectServer error"
							+ ", server:" + server
							+ ", clientConfig:" + clientConfig.toString(), e);
				}
			}
		} catch (Throwable e) {
			logger.error("[ClientHeartBeatTimer]: run error, clientConfig:" + clientConfig.toString(), e);
		}
	}
	
}
