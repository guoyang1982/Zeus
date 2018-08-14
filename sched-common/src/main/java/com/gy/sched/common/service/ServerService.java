package com.gy.sched.common.service;

import com.gy.sched.common.domain.ExecutableTask;
import com.gy.sched.common.domain.Machine;
import com.gy.sched.common.domain.result.Result;
import com.gy.sched.common.domain.store.Job;
import com.gy.sched.common.domain.store.TaskSnapshot;

import java.util.Date;
import java.util.Map;

/**
 * 服务端通用基础服务
 *
 */
public interface ServerService {

	/**
	 * 建立连接
	 * @param accessKey
	 * @return
	 */
	public Result<Boolean> connect(String accessKey);
	
	/**
	 * 注册jobMap
	 * @param machine
	 * @param jobMap
	 * @return
	 */
	public Result<Boolean> registerJobs(Machine machine, Map<String, String> jobMap);

	/**
	 * 注册创建job
	 * @return
	 */
	public Result<Boolean> registerAndCreateJobs(Job job);

	/**
	 * 任务ACK确认
	 * @param taskSnapshot
	 * @return
	 */
	public Result<Boolean> acknowledge(TaskSnapshot taskSnapshot);

	/**
	 * 批量任务ACK确认
	 * @param executableTask
	 * @param status
	 * @return
	 */
	public Result<Boolean> batchAcknowledge(ExecutableTask executableTask, int status);

//	/**
//	 * 发送任务列表到服务端
//	 * @param executableTask
//	 * @return
//	 */
//	public Result<Boolean> send(ExecutableTask executableTask);
//
//	/**
//	 * 通知服务器有任务了
//	 * @param executableTask
//	 * @return
//	 */
//	public Result<Boolean> notifyEvent(ExecutableTask executableTask);

	/**
	 * 从服务端拉取任务快照列表
	 * @param executableTask
	 * @return
	 */
	public Result<ExecutableTask> pull(ExecutableTask executableTask);

//	/**
//	 * 设置全局用户自定义参数
//	 * @param jobInstanceSnapshot
//	 * @param globalArguments
//	 * @return
//	 */
//	public Result<Boolean> setGlobalArguments(JobInstanceSnapshot jobInstanceSnapshot, String globalArguments);
//
//	/**
//	 * 获取设置的全局变量
//	 * @param jobInstanceSnapshot
//	 * @return
//	 */
//	public Result<String> getGlobalArguments(JobInstanceSnapshot jobInstanceSnapshot);
//
//	/**
//	 * 移除活跃任务
//	 * @param key
//	 * @return
//	 */
//	public Result<Boolean> removeLivingTask(ServerJobInstanceMapping.JobInstanceKey key);

	/**
	 * call依赖的JOB;
	 * @param beforeJobId
	 * @param dependencyJobId
	 * @param jobInstanceId
	 * @param fireTime
	 * @param lastJobId
	 * @param uniqueId
	 * @return
	 */
	public Result<Boolean> callDependencyJob(long beforeJobId, long dependencyJobId, long jobInstanceId, Date fireTime, long lastJobId, String uniqueId);
	
//	/**
//	 * 获取分组下面的Job列表
//	 * @param groupId
//	 * @return
//	 */
	//public List<Job> acquireJobList(String groupId);
	
	public int stopAllInstance(long jobId);
	
	//public List<RemoteMachine> getRemoteMachines(String groupId, long jobId);
	
	public Result<Boolean> warningSwitch(boolean warningSwitch);
	
	/**
	 * 读日志文件
	 * @param jobId
	 * @param instanceId
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	//public QueryLogResult readLog(long jobId, long instanceId, int pageNumber, int pageSize);
	
}
