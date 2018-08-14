package com.gy.sched.server.store.mysql;


import com.gy.sched.common.domain.store.Job;
import com.gy.sched.common.exception.AccessException;
import com.gy.sched.server.context.ServerContext;
import com.gy.sched.server.store.JobAccess;

import java.util.List;

/**
 * Job信息访问接口
 * Mysql实现
 */
public class JobAccess4Mysql implements JobAccess, ServerContext {

	/**
	 * 插入
	 */
	public long insert(Job job) throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClient()
					.insert("Job.insert", job);
		} catch (Throwable e) {
			throw new AccessException("[insert]: error", e);
		}
		if(null == result) {
			return 0L;
		}
		return result;
	}

	/**
	 * 根据id查询Job
	 */
//	@Override
//	public Job queryJobById(Job query) throws AccessException {
//		Job job = null;
//		try {
//			job = (Job)sqlMapClients.getSqlMapClientMeta()
//					.queryForObject("Job.queryJobById", query);
//		} catch (Throwable e) {
//			throw new AccessException("[queryJobById]: error", e);
//		}
//		return job;
//	}
//
//	/**
//	 * 查询分组所有Job
//	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Job> queryJobByGroupId(Job query) throws AccessException {
//		List<Job> jobList = null;
//		try {
//			jobList = (List<Job>)sqlMapClients.getSqlMapClientMeta()
//					.queryForList("Job.queryJobByGroupId", query);
//		} catch (Throwable e) {
//			throw new AccessException("[queryJobByGroupId]: error", e);
//		}
//		return jobList;
//	}
//
//	/**
//	 * 根据TaskName查询Job
//	 */
//	@SuppressWarnings("deprecation")
//	@Override
//	public Job queryJobByTaskName(Job query) throws AccessException {
//		Job job = null;
//		try {
//			job = (Job)sqlMapClients.getSqlMapClientMeta()
//					.queryForObject("Job.queryJobByTaskName", query);
//		} catch (Throwable e) {
//			throw new AccessException("[queryJobByTaskName]: error", e);
//		}
//		return job;
//	}
//
//	/**
//	 * 更新
//	 */
//	public int update(Job job) throws AccessException {
//		int result = 0;
//		try {
//			result = sqlMapClients.getSqlMapClientMeta()
//					.update("Job.update", job);
//		} catch (Throwable e) {
//			throw new AccessException("[update]: error", e);
//		}
//		return result;
//	}
//
//	/**
//	 * 更新JobStatus
//	 */
//	@Override
//	public int updateJobStatus(Job job) throws AccessException {
//		int result = 0;
//		try {
//			result = sqlMapClients.getSqlMapClientMeta()
//					.update("Job.updateJobStatus", job);
//		} catch (Throwable e) {
//			throw new AccessException("[update]: error", e);
//		}
//		return result;
//	}
//
//	/**
//	 * 删除
//	 */
//	public int delete(Job job) throws AccessException {
//		int result = 0;
//		try {
//			result = sqlMapClients.getSqlMapClientMeta()
//					.delete("Job.delete", job);
//		} catch (Throwable e) {
//			throw new AccessException("[delete]: error", e);
//		}
//		return result;
//	}
//
//	@Override
//	public int countJob(Job query) throws AccessException {
//		int result = 0;
//		try {
//			result = (Integer) sqlMapClients.getSqlMapClientMeta()
//					.queryForObject("Job.jobCount", query);
//		} catch (Throwable e) {
//			throw new AccessException("[jobCount]: error", e);
//		}
//		return result;
//	}
//
	@SuppressWarnings("unchecked")
	public List<Job> query(Job query) throws AccessException {
		List<Job> jobList = null;
		try {
			jobList = (List<Job>)sqlMapClients.getSqlMapClient()
					.queryForList("Job.query", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return jobList;
	}

	@Override
	public Job queryJobByAppIdAndEx(Job query) throws AccessException {
		Job job = null;
		try {
			job = (Job)sqlMapClients.getSqlMapClient()
					.queryForObject("Job.queryJobByAppIdAndEx", query);
		} catch (Throwable e) {
			throw new AccessException("[queryJobByAppIdAndEx]: error", e);
		}
		return job;
 	}

}
