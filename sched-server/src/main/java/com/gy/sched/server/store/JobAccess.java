package com.gy.sched.server.store;


import com.gy.sched.common.domain.store.Job;
import com.gy.sched.common.exception.AccessException;

import java.util.List;

/**
 * Job信息访问接口
 */
public interface JobAccess {

	/**
	 * 插入
	 * @param job
	 * @return
	 * @throws AccessException
	 */
	public long insert(Job job) throws AccessException;
	
	public List<Job> query(Job jobPage) throws AccessException;

	/**
	 * 查询根据应用id和时间
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public Job queryJobByAppIdAndEx(Job query) throws AccessException;
//
//	/**
//	 * 根据TaskName查询Job
//	 * @param query
//	 * @return
//	 * @throws AccessException
//	 */
//	public Job queryJobByTaskName(Job query) throws AccessException;
//
//	/**
//	 * 根据id查询Job
//	 * @param query
//	 * @return
//	 * @throws AccessException
//	 */
//	public Job queryJobById(Job query) throws AccessException;
//
//	/**
//	 * 计数Job;
//	 * @param query
//	 * @return
//	 */
//	public int countJob(Job query) throws AccessException;
//
//	/**
//	 * 更新
//	 * @param job
//	 * @return
//	 * @throws AccessException
//	 */
//	public int update(Job job) throws AccessException;
//
//	/**
//	 * 更新JobStatus
//	 * @param job
//	 * @return
//	 * @throws AccessException
//	 */
//	public int updateJobStatus(Job job) throws AccessException;
//
//	/**
//	 * 删除
//	 * @param job
//	 * @return
//	 * @throws AccessException
//	 */
//	public int delete(Job job) throws AccessException;
//
}
