package com.gy.sched.server.remoting;

import com.gy.sched.common.domain.result.Result;
import com.gy.sched.common.domain.store.DesignatedMachine;
import com.gy.sched.common.util.RemotingUtil;
import com.gy.sched.server.remoting.processor.ServerRequestProcessor;
import com.gy.sched.common.constants.Constants;
import com.gy.sched.common.domain.Machine;
import com.gy.sched.common.domain.remoting.RemoteMachine;
import com.gy.sched.common.domain.result.ResultCode;
import com.gy.sched.common.exception.InitException;
import com.gy.sched.common.exception.RemotingSendRequestException;
import com.gy.sched.common.exception.RemotingTimeoutException;
import com.gy.sched.common.proxy.ProxyInterface;
import com.gy.sched.common.remoting.netty.NettyRemotingServer;
import com.gy.sched.common.remoting.netty.NettyServerConfig;
import com.gy.sched.common.remoting.protocol.RemotingCommand;
import com.gy.sched.server.config.ServerConfig;
import com.gy.sched.server.context.ServerContext;
import com.gy.sched.server.remoting.listener.ServerChannelEventListener;
import com.gy.sched.server.remoting.proxy.ServerProxyInterceptor;
import com.gy.sched.server.remoting.timer.HeartBeatTimer;
import io.netty.channel.Channel;
import jodd.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 服务端远程通信
 */
public class ServerRemoting implements ServerContext, Constants {

    private static final Log logger = LogFactory.getLog("serverRemoting");
    /**
     * 服务器配置
     */
    public static final ServerConfig serverConfig = new ServerConfig();
    /**
     * 机器分组映射表
     */
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, RemoteMachine>> machineGroupTable =
            new ConcurrentHashMap<String, ConcurrentHashMap<String, RemoteMachine>>();

    /**
     * 指定机器映射表
     */
    private final ConcurrentHashMap<String, ConcurrentHashMap<Long, List<DesignatedMachine>>> designatedMachinesTable =
            new ConcurrentHashMap<String, ConcurrentHashMap<Long, List<DesignatedMachine>>>();

    /**
     * 有效指定机器映射表
     */
    private final ConcurrentHashMap<Long, List<RemoteMachine>> availableDesignatedMachinesTable =
            new ConcurrentHashMap<Long, List<RemoteMachine>>();

    /**
     * 机器与Job映射表
     */
    private final ConcurrentHashMap<Long, ConcurrentHashMap<Machine, RemoteMachine>> machineJobTable =
            new ConcurrentHashMap<Long, ConcurrentHashMap<Machine, RemoteMachine>>();

    /**
     * 远程通信服务端
     */
    private NettyRemotingServer server = null;

    /**
     * 定时调度服务
     */
    private ScheduledExecutorService executorService = Executors
            .newScheduledThreadPool(HEART_BEAT_THREAD_AMOUNT, new ThreadFactory() {

                int index = 0;

                public Thread newThread(Runnable runnable) {

                    index++;

                    return new Thread(runnable, HEART_BEAT_THREAD_NAME + index);
                }

            });

    /**
     * 服务端代理调用接口
     */
    //private InvocationHandler invocationHandler = new ServerInvocationHandler();
    private ProxyInterface proxyInterface = new ServerProxyInterceptor();
    /**
     * RPC响应线程池
     */
    private ThreadPoolExecutor executors = null;

    /**
     * 请求队列
     */
    private LinkedBlockingQueue<Runnable> requestQueue = new LinkedBlockingQueue<Runnable>();

    /**
     * 初始化
     *
     * @throws InitException
     */
    public void init() throws InitException {

        /** 初始化远程通信服务端 */
        initRemotingServer();

        /** 初始化心跳定时器 */
        initHeartBeatTimer();

        /** 初始化任务探测 */
        // initRemoteTaskSniffer();

        /** 初始化指定机器列表 */
        //initDesignatedMachineList();

    }

    /**
     * 初始化远程通信服务端
     *
     * @throws InitException
     */
    private void initRemotingServer() throws InitException {
        NettyServerConfig config = new NettyServerConfig();
        /** 设置监听端口 */
        config.setListenPort(serverConfig.getListenerPort());

        ServerRequestProcessor processor = new ServerRequestProcessor(requestQueue);
        processor.init();

        ServerChannelEventListener listener = new ServerChannelEventListener();

        server = new NettyRemotingServer(config, listener);

        executors = new ThreadPoolExecutor(serverConfig.getRemotingThreads(), serverConfig.getRemotingThreads(),
                0L, TimeUnit.MILLISECONDS,
                requestQueue,
                new ThreadFactory() {

                    int index = 0;

                    @Override
                    public Thread newThread(Runnable runnable) {
                        index++;
                        return new Thread(runnable, REMOTING_THREAD_NAME + index);
                    }

                });

        server.registerProcessor(REQUEST_CODE, processor, executors);

        try {
            server.start();
        } catch (Throwable e) {
            throw new InitException("[ServerRemoting]: init error", e);
        }
    }

    /**
     * 初始化心跳定时器
     *
     * @throws InitException
     */
    private void initHeartBeatTimer() throws InitException {
        try {
            executorService.scheduleAtFixedRate(new HeartBeatTimer(),
                    0L, serverConfig.getHeartBeatIntervalTime(), TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            throw new InitException("[ServerRemoting]: initHeartBeatTimer error"
                    + ", heartBeatIntervalTime:" + serverConfig.getHeartBeatIntervalTime(), e);
        }
        logger.warn("[ServerRemoting]: initHeartBeatTimer success"
                + ", heartBeatIntervalTime:" + serverConfig.getHeartBeatIntervalTime());
    }

    /**
     * 初始化任务探测
     * @throws InitException
     */
//    private void initRemoteTaskSniffer() throws InitException {
//        try {
//            LivingTaskManager.getSingleton().startSniffer(executorService);
//        } catch (Throwable e) {
//            throw new InitException("[ServerRemoting]: initRemoteTaskSniffer error"
//                    + ", heartBeatIntervalTime:" + serverConfig.getHeartBeatIntervalTime(), e);
//        }
//        logger.warn("[ServerRemoting]: initRemoteTaskSniffer success"
//                + ", initRemoteTaskSniffer:" + serverConfig.getHeartBeatIntervalTime());
//    }

    /**
     * 初始化指定机器列表
     */
//    private void initDesignatedMachineList() throws InitException {
//    	long id = 0L;
//    	List<DesignatedMachine> designatedMachineList = designatedMachineManager.loadDesignatedMachineList(id);
//    	while(! CollectionUtils.isEmpty(designatedMachineList)) {
//
//    		for(DesignatedMachine designatedMachine : designatedMachineList) {
//
//    			String groupId = GroupIdUtil.generateGroupId(serverConfig.getClusterId(),
//    					serverConfig.getServerGroupId(), serverConfig.getJobBackupAmount(), designatedMachine.getClientGroupId());
//    			ConcurrentHashMap<Long, List<DesignatedMachine>> designatedMachinesMap = this.designatedMachinesTable.get(groupId);
//    			if(null == designatedMachinesMap) {
//    				designatedMachinesMap = new ConcurrentHashMap<Long, List<DesignatedMachine>>();
//    				this.designatedMachinesTable.put(groupId, designatedMachinesMap);
//    			}
//
//    			List<DesignatedMachine> designatedMachines = designatedMachinesMap.get(designatedMachine.getJobId());
//    			if(null == designatedMachines) {
//    				designatedMachines = new ArrayList<DesignatedMachine>();
//    				designatedMachinesMap.put(designatedMachine.getJobId(), designatedMachines);
//    			}
//
//    			designatedMachines.add(designatedMachine);
//    		}
//
//    		id = ListUtil.acquireLastObject(designatedMachineList).getId();
//    		designatedMachineList = designatedMachineManager.loadDesignatedMachineList(id);
//    	}
//    }

    /**
     * 代理接口
     *
     * @param interfaceClass
     * @return
     */
    public <T> T proxyInterface(Class<T> interfaceClass) {
        //return proxyService.proxyInterface(interfaceClass, invocationHandler);
        return proxyService.cglibProxyInterface(interfaceClass, proxyInterface);
    }

    /**
     * 建立连接并缓存客户端地址列表
     *
     * @param remoteMachine
     * @return
     */
    public Result<Boolean> connect(RemoteMachine remoteMachine) {
        ConcurrentHashMap<String, RemoteMachine> machineMap = machineGroupTable.get(remoteMachine.getAppName());
        if (null == machineMap) {
            machineMap = new ConcurrentHashMap<String, RemoteMachine>();
            machineGroupTable.put(remoteMachine.getAppName(), machineMap);
        }

        machineMap.put(remoteMachine.getRemoteAddress(), remoteMachine);

        logger.info("[ServerRemoting]: connect, remoteMachine:" + remoteMachine.toShortString());
        return new Result<Boolean>(true, ResultCode.SUCCESS);
    }

    /**
     * 注册jobMap
     * @param machine
     * @param jobMap
     * @param remoteMachine
     * @return
     */
//	@SuppressWarnings("rawtypes")
//	public Result<Boolean> registerJobs(Machine machine, Map<String, String> jobMap, RemoteMachine remoteMachine) {
//
//		Iterator iterator = jobMap.entrySet().iterator();
//		while(iterator.hasNext()) {
//			Map.Entry entry = (Map.Entry)iterator.next();
//
//			String taskName = (String)entry.getKey();
//
//			//添加通信信道
//			addChannel(machine, taskName, remoteMachine);
//		}
//
//		return new Result<Boolean>(true, ResultCode.SUCCESS);
//	}

    /**
     * 添加通信信道
     * @param machine
     * @param taskName
     * @param remoteMachine
     */
//	private void addChannel(Machine machine, String taskName, RemoteMachine remoteMachine) {
//		Job query = new Job();
//		query.setTaskName(taskName);
//
//		Job job = null;
//		try {
//			job = store.getJobAccess().queryJobByTaskName(query);
//		} catch (Throwable e) {
//			logger.error("[ServerRemoting]: addChannel queryJobByTaskName error, taskName:" + taskName, e);
//		}
//
//		if(null == job) {
//			logger.error("[ServerRemoting]: addChannel queryJobByTaskName failed, taskName:" + taskName);
//			return ;
//		}
//
//		ConcurrentHashMap<Machine, RemoteMachine> machineMap = this.machineJobTable.get(job.getId());
//		if(CollectionUtils.isEmpty(machineMap)) {
//			machineMap = new ConcurrentHashMap<Machine, RemoteMachine>();
//			this.machineJobTable.put(job.getId(), machineMap);
//		}
//
//		machine.setRemoteAddress(remoteMachine.getRemoteAddress());
//
//		machineMap.put(machine, remoteMachine);
//
//		/** 写客户端节点 */
//        zookeeper.writeClient(job.getId(), machine);
//	}

    /**
     * 增加有效指定机器
     *
     * @param remoteMachine
     */
//    @SuppressWarnings({"rawtypes", "unchecked"})
//    private void addAvailableDesignatedMachine(RemoteMachine remoteMachine) {
//        ConcurrentHashMap<Long, List<DesignatedMachine>> designatedMachinesMap = this.designatedMachinesTable.get(remoteMachine.getGroupId());
//        if (CollectionUtils.isEmpty(designatedMachinesMap)) {
//            return;
//        }
//
//        Iterator iterator = designatedMachinesMap.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry entry = (Map.Entry) iterator.next();
//            Long jobId = (Long) entry.getKey();
//            List<DesignatedMachine> designatedMachines = (List<DesignatedMachine>) entry.getValue();
//            if (CollectionUtils.isEmpty(designatedMachines)) {
//                continue;
//            }
//            for (DesignatedMachine designatedMachine : designatedMachines) {
//                String remoteIp = RemotingUtil.parseIpFromAddress(remoteMachine.getRemoteAddress());
//                if (remoteIp.equals(designatedMachine.getMachine())) {
//                    List<RemoteMachine> remoteMachineList = this.availableDesignatedMachinesTable.get(jobId);
//                    if (null == remoteMachineList) {
//                        remoteMachineList = new ArrayList<RemoteMachine>();
//                        this.availableDesignatedMachinesTable.put(jobId, remoteMachineList);
//                    }
//                    if (!remoteMachineList.contains(remoteMachine)) {
//                        remoteMachineList.add(remoteMachine);
//                    }
//                    break;
//                }
//            }
//        }
//    }

    /**
     * 刷新有效指定机器列表
     *
     * @param groupId
     * @param jobId
     */
    @SuppressWarnings("rawtypes")
    public void refreshAvailableDesignatedMachine(String groupId, long jobId) {
        ConcurrentHashMap<Long, List<DesignatedMachine>> designatedMachinesMap = this.designatedMachinesTable.get(groupId);
        if (CollectionUtils.isEmpty(designatedMachinesMap)) {
            this.availableDesignatedMachinesTable.remove(jobId);
            return;
        }

        List<DesignatedMachine> designatedMachineList = designatedMachinesMap.get(jobId);
        if (CollectionUtils.isEmpty(designatedMachineList)) {
            this.availableDesignatedMachinesTable.remove(jobId);
            return;
        }

        ConcurrentHashMap<String, RemoteMachine> machineMap = this.machineGroupTable.get(groupId);
        if (CollectionUtils.isEmpty(machineMap)) {
            this.availableDesignatedMachinesTable.remove(jobId);
            return;
        }

        List<RemoteMachine> remoteMachineList = new ArrayList<RemoteMachine>();
        for (DesignatedMachine designatedMachine : designatedMachineList) {
            Iterator iterator = machineMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String remoteAddress = (String) entry.getKey();
                RemoteMachine remoteMachine = (RemoteMachine) entry.getValue();
                String remoteIp = RemotingUtil.parseIpFromAddress(remoteAddress);
                if (remoteIp.equals(designatedMachine.getMachine())) {
                    remoteMachineList.add(remoteMachine);
                }
            }
        }
        this.availableDesignatedMachinesTable.put(jobId, remoteMachineList);
        logger.info("[ServerRemoting]: refreshAvailableDesignatedMachine"
                + ", groupId:" + groupId
                + ", jobId:" + jobId
                + ", remoteMachineList:" + remoteMachineList);
    }


    /**
     * 根据groupId和JobId获取远程机器列表
     * @return
     */
    public List<RemoteMachine> getRemoteMachines(String appName) {

        return this.getMachineGroup(appName);

//        /** 如果该分组没指定客户端列表就返回整个客户端集群列表 */
//        ConcurrentHashMap<Long, List<DesignatedMachine>> designatedMachinesMap = this.designatedMachinesTable.get(groupId);
//        if (CollectionUtils.isEmpty(designatedMachinesMap)) {
//            return this.getMachineGroup(groupId);
//        }
//
//        /** 如果该job没指定客户端列表就返回整个客户端集群列表 */
//        List<DesignatedMachine> designatedMachines = designatedMachinesMap.get(jobId);
//        if (CollectionUtils.isEmpty(designatedMachines)) {
//            return this.getMachineGroup(groupId);
//        }
//
//        List<RemoteMachine> remoteMachineList = this.availableDesignatedMachinesTable.get(jobId);
//        if (CollectionUtils.isEmpty(remoteMachineList)) {
//            int policy = designatedMachines.get(0).getPolicy();
//            if (DESIGNATED_MACHINE_POLICY_MIGTATION == policy) {
//                return this.getMachineGroup(groupId);
//            } else if (DESIGNATED_MACHINE_POLICY_NOT_MIGTATION == policy) {
//                return remoteMachineList;
//            }
//        }

       // return remoteMachineList;
    }

    /**
     * 获取分组机器列表
     *
     * @param appName
     * @return
     */
    private List<RemoteMachine> getMachineGroup(String appName) {
        ConcurrentHashMap<String, RemoteMachine> machineMap = machineGroupTable.get(appName);
        if (null == machineMap) {
            return null;
        }
        return new ArrayList<RemoteMachine>(machineMap.values());
    }

    /**
     * 删除连接
     *
     * @param remoteAddress
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void deleteConnection(String remoteAddress) {
        if (StringUtil.isBlank(remoteAddress)) {
            return;
        }
        Iterator iterator = machineGroupTable.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String groupId = (String) entry.getKey();
            ConcurrentHashMap<String, RemoteMachine> machineMap = (ConcurrentHashMap<String, RemoteMachine>) entry.getValue();
            machineMap.remove(remoteAddress);

            logger.info("[ServerRemoting]: deleteConnection, remoteAddress:" + remoteAddress);
        }
    }

    /**
     * 删除连接
     *
     * @param remoteMachine
     */
    public void deleteConnection(RemoteMachine remoteMachine) {
        ConcurrentHashMap<String, RemoteMachine> machineMap = machineGroupTable.get(remoteMachine.getAppName());
        if (null == machineMap) {
            return;
        }
        machineMap.remove(remoteMachine.getRemoteAddress());
        RemotingUtil.closeChannel(remoteMachine.getChannel());

        logger.info("[ServerRemoting]: deleteConnection, remoteMachine:" + remoteMachine);
    }

    /**
     * 远程方法同步调用
     *
     * @param channel
     * @param request
     * @param timeoutMillis
     * @return
     * @throws InterruptedException
     * @throws RemotingSendRequestException
     * @throws RemotingTimeoutException
     */
    public RemotingCommand invokeSync(final Channel channel, final RemotingCommand request,
                                      final long timeoutMillis) throws InterruptedException, RemotingSendRequestException,
            RemotingTimeoutException {
        return server.invokeSync(channel, request, timeoutMillis);
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<String, RemoteMachine>> getMachineGroupTable() {
        return machineGroupTable;
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<Long, List<DesignatedMachine>>> getDesignatedMachinesTable() {
        return designatedMachinesTable;
    }

}
