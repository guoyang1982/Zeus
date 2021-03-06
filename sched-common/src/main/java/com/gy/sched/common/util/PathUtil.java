package com.gy.sched.common.util;


import com.gy.sched.common.constants.Constants;

import java.text.MessageFormat;

/**
 * 路径相关工具
 *
 */
public class PathUtil implements Constants {

    private static final String JOB_INSTANCE_LOCK_PATH = FORWARD_SLASH + DEFAULT_ZK_ROOT_PATH + "/locks/job_instance_{0}_{1}";

    /**
	 * 获取用户home目录配置文件路径
	 * @param fileName
	 * @return
	 */
	public static String getJSTHomeConfigPath(String fileName) {
		String separator = System.getProperty(FILE_SEPARATOR);
		return System.getProperties().getProperty(USER_HOME) 
				+ separator + DTS_CLIENT 
				+ separator + JST_DTS_CONFIG 
				+ separator + fileName;
	}
	
	/**
	 * 获取用户目录日志文件根路径
	 * @return
	 */
	public static String getHomeLoggerRootPath() {
		String separator = System.getProperty(FILE_SEPARATOR);
		return System.getProperties().getProperty(USER_HOME) 
				+ separator + DTS_CLIENT 
				+ separator + DTS_LOGS;
	}
	
	/**
	 * 获取用户目录日志文件路径
	 * @param jobId
	 * @param file
	 * @return
	 */
	public static String getHomeLoggerPath(long jobId, String file) {
		String separator = System.getProperty(FILE_SEPARATOR);
		return System.getProperties().getProperty(USER_HOME) 
				+ separator + DTS_CLIENT 
				+ separator + DTS_LOGS 
				+ separator + jobId 
				+ separator + file;
	}
	
	/**
	 * 获取某个Task的日志目录
	 * @param jobId
	 * @return
	 */
	public static String getHomeLoggerTaskPath(long jobId) {
		String separator = System.getProperty(FILE_SEPARATOR);
		return System.getProperties().getProperty(USER_HOME) 
				+ separator + DTS_CLIENT 
				+ separator + DTS_LOGS 
				+ separator + jobId;
	}
	
	/**
	 * 获取用户目录日志文件路径
	 * @param jobId
	 * @param instanceId
	 * @param ext
	 * @return
	 */
	public static String getHomeLoggerPath(long jobId, long instanceId, String ext) {
		String separator = System.getProperty(FILE_SEPARATOR);
		return System.getProperties().getProperty(USER_HOME) 
				+ separator + DTS_CLIENT 
				+ separator + DTS_LOGS 
				+ separator + jobId 
				+ separator + instanceId + ext;
	}
    
    public static String getLoggerPath() {
		String separator = System.getProperty(FILE_SEPARATOR);
		return System.getProperties().getProperty(USER_HOME) + separator + "logs";
	}
    
	/**
	 * 获取用户home目录配置文件路径
	 * @param fileName
	 * @return
	 */
	public static String getHomeConfigPath(String fileName) {
		String separator = System.getProperty(FILE_SEPARATOR);
		return System.getProperties().getProperty(USER_HOME) + separator + DTS_CONFIG + separator + fileName;
	}
	
	/**
	 * 获取服务器集群路径
	 * 列如/zk-dts-root/server-cluster/1
	 * @param serverClusterId
	 * @return
	 */
	public static String getServerClusterPath(long serverClusterId) {
		return FORWARD_SLASH + DEFAULT_ZK_ROOT_PATH + FORWARD_SLASH + ZK_SERVER_CLUSTER + FORWARD_SLASH + serverClusterId;
	}
	
	/**
	 * 获取服务器集群
	 * 列如/zk-dts-root/server-cluster
	 * @return
	 */
	public static String getServerGroupPath() {
		return FORWARD_SLASH + DEFAULT_ZK_ROOT_PATH + FORWARD_SLASH + ZK_SERVER_CLUSTER;
	}
	
	/**
	 * 获取服务器节点路径
	 * 列如/zk-dts-root/server-cluster/10.232.10.184:52014
	 * @param server
	 * @return
	 */
	public static String getServerPath(String server) {
		return FORWARD_SLASH + DEFAULT_ZK_ROOT_PATH + FORWARD_SLASH + ZK_SERVER_CLUSTER + FORWARD_SLASH + server;
	}
	
	/**
	 * /zk-dts-root/locks
	 * @return
	 */
	public static String getJobInstanceLockPath() {
		return FORWARD_SLASH + DEFAULT_ZK_ROOT_PATH + FORWARD_SLASH + ZK_LOCKS;
    }
	
    /**
     * /zk-dts-root/locks/job_instance_11
     */
    public static String getJobInstanceLockPath(String instanceId, String timeInSecond) {
        return MessageFormat.format(JOB_INSTANCE_LOCK_PATH, instanceId, timeInSecond);
    }
    
    /**
     * 获取Job实例路径
     * 列如/zk-dts-root/job-instance-list/job_instance_11
     * @param instanceId
     * @return
     */
    public static String getJobInstancePath(String instanceId) {
    	return FORWARD_SLASH + DEFAULT_ZK_ROOT_PATH + FORWARD_SLASH + ZK_JOB_INSTANCE_LIST + FORWARD_SLASH + instanceId;
    }
    
    /**
     * 获取Job实例客户端路径
     * 列如/zk-dts-root/job-instance-list/job_instance_11/10.232.10.182
     * @param instanceId
     * @param client
     * @return
     */
    public static String getJobInstanceClientPath(String instanceId, String client) {
    	return FORWARD_SLASH + DEFAULT_ZK_ROOT_PATH + FORWARD_SLASH + ZK_JOB_INSTANCE_LIST + FORWARD_SLASH + instanceId + FORWARD_SLASH + client;
    }

    /**
     * 获取客户端分组在ZK上路径
     * 列如列如/zk-dts-root/client-cluster/2-4-1-46
     * @param clientGroup
     * @return
     */
    public static String getClientGroupPath(String clientGroup) {
        return FORWARD_SLASH + DEFAULT_ZK_ROOT_PATH + FORWARD_SLASH + ZK_CLIENT_CLUSTER + FORWARD_SLASH + clientGroup;
    }
    
    /**
     * 获取客户端在ZK上路径
     * 列如列如/zk-dts-root/client-cluster/2-4-1-46/10.232.10.182:52018
     * @param clientGroup
     * @param client
     * @return
     */
    public static String getClientPath(String clientGroup, String client) {
        return FORWARD_SLASH + DEFAULT_ZK_ROOT_PATH + FORWARD_SLASH + ZK_CLIENT_CLUSTER + FORWARD_SLASH + clientGroup + FORWARD_SLASH + client;
    }
	
    /**
     * 控制台路径
     * 列如/zk-dts-root/console-cluster
     * @return
     */
    public static String getConsolePath() {
		return FORWARD_SLASH + DEFAULT_ZK_ROOT_PATH + FORWARD_SLASH + ZK_CONSOLE_CLUSTER;
	}
    
    /**
     * 控制台IP路径
     * 列如/zk-dts-root/console-cluster/10.232.10.182
     * @param ip
     * @return
     */
    public static String getConsoleIpPath(String ip) {
		return FORWARD_SLASH + DEFAULT_ZK_ROOT_PATH + FORWARD_SLASH + ZK_CONSOLE_CLUSTER + FORWARD_SLASH + ip;
	}
    
    /**
     * 任务列表目录
     * 例如/zk-dts-root/task-list
     * @return
     */
    public static String getTaskListPath() {
    	return FORWARD_SLASH + DEFAULT_ZK_ROOT_PATH + FORWARD_SLASH + ZK_TASK_LIST;
    }
    
    /**
     * 任务目录
     * 例如/zk-dts-root/task-list/1
     * @param taskId
     * @return
     */
    public static String getTaskPath(long taskId) {
    	return FORWARD_SLASH + DEFAULT_ZK_ROOT_PATH + FORWARD_SLASH + ZK_TASK_LIST + FORWARD_SLASH + taskId;
    }
    
    /**
     * 任务主机列表目录
     * 例如/zk-dts-root/task-list/1/host-list
     * @param taskId
     * @return
     */
    public static String getTaskHostListPath(long taskId) {
    	return FORWARD_SLASH + DEFAULT_ZK_ROOT_PATH + FORWARD_SLASH + ZK_TASK_LIST + FORWARD_SLASH + taskId + FORWARD_SLASH + ZK_HOST_LIST;
    }
    
    /**
     * 机器目录
     * 例如/zk-dts-root/task-list/1/host-list/ECS01
     * @param taskId
     * @param machineId
     * @return
     */
    public static String getTaskHostPath(long taskId, String machineId) {
    	return FORWARD_SLASH + DEFAULT_ZK_ROOT_PATH + FORWARD_SLASH + ZK_TASK_LIST + FORWARD_SLASH + taskId + FORWARD_SLASH + ZK_HOST_LIST + FORWARD_SLASH + machineId;
    }
    
}
