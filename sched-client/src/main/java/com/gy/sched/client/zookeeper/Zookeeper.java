package com.gy.sched.client.zookeeper;

import com.google.common.collect.Lists;
import com.gy.sched.client.context.ClientContext;
import com.gy.sched.common.constants.Constants;
import com.gy.sched.common.exception.InitException;
import com.gy.sched.common.util.PathUtil;
import com.gy.sched.common.zk.ZkConfig;
import com.gy.sched.common.zk.ZkManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Zookeeper
 *
 */
public class Zookeeper implements ClientContext, Constants {

	private static final Log logger = LogFactory.getLog(Zookeeper.class);
	
	private ZkManager zkManager = new ZkManager();
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		/** 初始化ZkManager */
		initZkManager();
		
	}
	
	/**
	 * 初始化ZkManager
	 * @throws InitException
	 */
	private void initZkManager() throws InitException {
		ZkConfig zkConfig = new ZkConfig();
		zkConfig.setZkHostsAutoChange(clientConfig.isZkHostsAutoChange());
		zkConfig.setNamespace(clientConfig.getNamespace());
		zkConfig.setZkHosts(clientConfig.getZkHosts());
		zkConfig.setZkConnectionTimeout(clientConfig.getZkConnectionTimeout());
		zkConfig.setZkSessionTimeout(clientConfig.getZkSessionTimeout());
		//zkConfig.setEnvironment(clientConfig.getEnvironment());
		zkConfig.setDomainName(clientConfig.getDomainName());
		zkConfig.setZkHostsSource(ZkConfig.ZK_HOSTS_DIAMOND_SOURCE);
		zkManager.setZkConfig(zkConfig);
		try {
			zkManager.init();
		} catch (Throwable e) {
			throw new InitException("[Zookeeper]: initZkManager error", e);
		}

		//设置zk地址列表
		clientConfig.setZkHosts(zkConfig.getZkHosts());
	}

	/**
	 * 获取当前服务器集群分组服务器列表
	 * @return
	 */
	public List<String> getServerList() {
		String serverGroupPath = PathUtil.getServerGroupPath();

		List<String> serverList = null;
		try {
			serverList = zkManager.getChildren(serverGroupPath);
		} catch (Throwable e) {
			logger.error("[Zookeeper]: getChildren error, serverGroupPath:" + serverGroupPath, e);
		}
		return serverList;
	}

	public ZkManager getZkManager() {
		return zkManager;
	}
	
}
