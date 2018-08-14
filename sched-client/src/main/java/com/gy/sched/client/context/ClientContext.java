package com.gy.sched.client.context;


import com.gy.sched.client.zookeeper.Zookeeper;
import com.gy.sched.common.proxy.ProxyService;
import com.gy.sched.common.service.ClientService;
import com.gy.sched.client.config.ClientConfig;
import com.gy.sched.client.executor.Executor;
import com.gy.sched.client.executor.job.factory.JobProcessorFactory;
import com.gy.sched.client.remoting.ClientRemoting;
import com.gy.sched.client.service.ClientServiceImpl;


/**
 * 客户端全局上下文
 * 可以放置全局使用的类
 *
 */
public interface ClientContext {

	/** 客户端各项参数配置 */
	public static final ClientConfig clientConfig = new ClientConfig();
	
	/** 代理服务 */
	public static final ProxyService proxyService = new ProxyService();
	
	/** 客户端远程通信 */
	public static final ClientRemoting clientRemoting = new ClientRemoting();
	
	/** 客户端通用基础服务 */
	public static final ClientService clientService = new ClientServiceImpl();
	
	/** Zookeeper */
	public static final Zookeeper zookeeper = new Zookeeper();

	/** Job处理器工厂 */
	public static final JobProcessorFactory jobProcessorFactory = new JobProcessorFactory();

	/** 执行任务容器 */
	public static final Executor executor = new Executor();
//
//	//执行日志
//	public static final ExecuteLogger executeLogger = new ExecuteLogger();
//
//	public static final HttpService httpService = new HttpService();
	
}
