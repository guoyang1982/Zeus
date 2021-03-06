package com.gy.sched.client.remoting;


import com.gy.sched.client.context.ClientContext;
import com.gy.sched.client.remoting.processor.ClientRequestProcessor;
import com.gy.sched.client.remoting.proxy.ClientProxyInterceptor;
import com.gy.sched.common.constants.Constants;
import com.gy.sched.common.context.InvocationContext;
import com.gy.sched.common.domain.result.Result;
import com.gy.sched.common.exception.InitException;
import com.gy.sched.common.exception.RemotingConnectException;
import com.gy.sched.common.exception.RemotingSendRequestException;
import com.gy.sched.common.exception.RemotingTimeoutException;
import com.gy.sched.common.proxy.ProxyInterface;
import com.gy.sched.common.proxy.ProxyService;
import com.gy.sched.common.remoting.netty.NettyClientConfig;
import com.gy.sched.common.remoting.netty.NettyRemotingClient;
import com.gy.sched.common.remoting.protocol.RemotingCommand;
import com.gy.sched.common.service.ServerService;
import com.gy.sched.common.util.StringUtil;
import com.gy.sched.client.remoting.timer.ClientHeartBeatTimer;
import com.gy.sched.common.domain.remoting.RemoteMachine;
import io.netty.channel.Channel;
import net.sf.cglib.proxy.MethodInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 客户端远程通信
 * @author tianyao.myc
 *
 */
public class ClientRemoting implements ClientContext, Constants {

	private static final Log logger = LogFactory.getLog(ClientRemoting.class);

	/** 代理服务 */
	public static final ProxyService proxyService = new ProxyService();

	/** 远程通信客户端 */
	private NettyRemotingClient client = null;
	
	/** 定时调度服务 */
	private ScheduledExecutorService dtsTimerService = Executors
			.newScheduledThreadPool(1, new ThreadFactory() {
				
				int index = 0;
				
				public Thread newThread(Runnable runnable) {
					index ++;
					return new Thread(runnable, HEART_BEAT_THREAD_NAME + index);
				}
				
			});
	
	/** 客户端代理调用接口 */
	//private InvocationHandler invocationHandler = new ClientInvocationHandler();
	private ProxyInterface proxyInterface = new ClientProxyInterceptor();


	private ServerService serverService = proxyInterface(ServerService.class);
	
	/** 心跳计数器映射表 */
	private ConcurrentHashMap<String, AtomicLong> heartBeatCounterTable4Increase 	= new ConcurrentHashMap<String, AtomicLong>();
	private ConcurrentHashMap<String, AtomicLong> heartBeatCounterTable4Compare 	= new ConcurrentHashMap<String, AtomicLong>();
	
	/** 服务器列表缓存 */
	private volatile List<String> serverListCache;
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		/** 初始化远程通信客户端 */
		initRemotingClient();
		
		/** 初始化连接 */
		initConnection();
		
		/** 初始化心跳定时器 */
		initHeartBeatTimer();
	}
	
	/**
	 * 初始化远程通信客户端
	 * @throws InitException
	 */
	private void initRemotingClient() throws InitException {
		NettyClientConfig config = new NettyClientConfig();
		
		ClientRequestProcessor processor = new ClientRequestProcessor();
		
		client = new NettyRemotingClient(config);
		client.registerProcessor(REQUEST_CODE, processor, Executors.newFixedThreadPool(clientConfig.getRemotingThreads(), new ThreadFactory(){

			int index = 0;
			
			@Override
			public Thread newThread(Runnable runnable) {
				index ++;
				
				Thread thread = new Thread(runnable, REMOTING_THREAD_NAME + index);
				
				//设置通信线程更高优先级
				thread.setPriority(10);
				
				return thread;
			}
			
		}));
		
		try {
			client.start();
		} catch (Throwable e) {
			throw new InitException("[ClientRemoting]: initRemotingClient error", e);
		}
	}
	
	/**
	 * 初始化连接
	 * 向服务端分组内所有机器建立长连接，并附带客户端分组信息
	 * @throws InitException
	 */
	private void initConnection() throws InitException {
		List<String> serverList = getServerList();
		if(CollectionUtils.isEmpty(serverList)) {
			logger.warn("[ClientRemoting]: initConnection error, serverList is empty");
			return ;
		}
		for(String server : serverList) {
			/** 连接服务器 */
			connectServer(server);
		}
		
	}
	
	/**
	 * 连接服务器
	 * @param server
	 */
	public void connectServer(String server) {
		InvocationContext.setRemoteMachine(new RemoteMachine(server));
		Result<Boolean> connectResult = serverService.connect(StringUtil.isBlank(clientConfig.getAccessKey()) ? NULL : clientConfig.getAccessKey());
		if(null == connectResult) {
			logger.error("[ClientRemoting]: connectServer failed, connectResult is null" 
					+ ", machineGroup:" + clientConfig.getAppName() + ", server:" + server);
			return ;
		}
		if(connectResult.getData().booleanValue()) {
			logger.warn("[ClientRemoting]: connectServer success"
					+ ", connectResult:" + connectResult.toString()
					+ ", machineGroup:" + clientConfig.getAppName() + ", server:" + server);
		} else {
			logger.error("[ClientRemoting]: connectServer failed"
					+ ", connectResult:" + connectResult.toString() 
					+ ", machineGroup:" + clientConfig.getAppName() + ", server:" + server);
		}
	}
	
	/**
	 * 从ZK获取服务器列表
	 * @return
	 */
	public List<String> getServerList() {
		if(CollectionUtils.isEmpty(this.serverListCache)) {
			this.serverListCache = zookeeper.getServerList();
		}
		return this.serverListCache;
	}
	
	/**
	 * 初始化心跳定时器
	 * @throws InitException
	 */
	private void initHeartBeatTimer() throws InitException {
		try {
			dtsTimerService.scheduleAtFixedRate(new ClientHeartBeatTimer(),
					clientConfig.getHeartBeatIntervalTime(),
					clientConfig.getHeartBeatIntervalTime(), TimeUnit.MILLISECONDS);
		} catch (Throwable e) {
			throw new InitException("[ClientRemoting]: initHeartBeatTimer error"
					+ ", heartBeatIntervalTime:" + clientConfig.getHeartBeatIntervalTime(), e);
		}
		logger.warn("[ClientRemoting]: initHeartBeatTimer success"
				+ ", heartBeatIntervalTime:" + clientConfig.getHeartBeatIntervalTime());
	}
	
	/**
	 * 代理接口
	 * @param interfaceClass
	 * @return
	 */
	public <T> T proxyInterface(Class<T> interfaceClass) {
		return proxyService.cglibProxyInterface(interfaceClass, proxyInterface);
	}
	
	/**
	 * 获取连接
	 * @param addr
	 * @return
	 * @throws InterruptedException
	 */
	public Channel getAndCreateChannel(final String addr) throws InterruptedException {
		return client.getAndCreateChannel(addr);
	}
	
	/**
	 * 远程方法同步调用
	 * @param addr
	 * @param request
	 * @param timeoutMillis
	 * @return
	 * @throws InterruptedException
	 * @throws RemotingConnectException
	 * @throws RemotingSendRequestException
	 * @throws RemotingTimeoutException
	 */
	public RemotingCommand invokeSync(String addr, final RemotingCommand request, long timeoutMillis)
            throws InterruptedException, RemotingConnectException, RemotingSendRequestException,
			RemotingTimeoutException {
		return client.invokeSync(addr, request, timeoutMillis);
	}

	/**
	 * 多线程环境同一个key拿到同一个counter
	 * @param key
	 * @return
	 */
	public AtomicLong getCounter(ConcurrentHashMap<String, AtomicLong> heartBeatCounter, String key) {
		AtomicLong counter = heartBeatCounter.get(key);
		if(null == counter) {
			counter = new AtomicLong(0L);
			AtomicLong existCounter = heartBeatCounter.putIfAbsent(key, counter);
			if(existCounter != null) {
				counter = existCounter;
			}
		}
		return counter;
	}

	public AtomicLong getHeartBeatCounter4Increase(String key) {
		return getCounter(heartBeatCounterTable4Increase, key);
	}

	public AtomicLong getHeartBeatCounter4Compare(String key) {
		return getCounter(heartBeatCounterTable4Compare, key);
	}

	public void setServerListCache(List<String> serverListCache) {
		this.serverListCache = serverListCache;
	}
	
}
