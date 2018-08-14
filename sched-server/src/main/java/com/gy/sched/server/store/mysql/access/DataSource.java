package com.gy.sched.server.store.mysql.access;

import com.alibaba.druid.pool.DruidDataSource;
import com.gy.sched.common.constants.Constants;
import com.gy.sched.common.exception.InitException;
import com.gy.sched.server.context.ServerContext;

/**
 * 数据源
 */
public class DataSource implements ServerContext, Constants {

	/** TDDL数据源 */
	private final DruidDataSource druidDataSource = new DruidDataSource();

	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
			//初始化RDS
			initRds();
	}
	
	/**
	 * 初始化RDS
	 * @throws InitException
	 */
	private void initRds() throws InitException {
		
		druidDataSource.setDriverClassName(serverConfig.getDriverClassName());
		druidDataSource.setUrl(serverConfig.getUrl());
		druidDataSource.setUsername(serverConfig.getUsername());
		druidDataSource.setPassword(serverConfig.getPassword());
		druidDataSource.setMaxActive(serverConfig.getMaxActive());
		

		
	}

	public DruidDataSource getDruidDataSource() {
		return druidDataSource;
	}

}
