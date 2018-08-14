package com.gy.sched.common.domain.store;

import com.gy.sched.common.remoting.protocol.RemotingSerializable;
import com.gy.sched.common.util.TimeUtil;
import com.gy.sched.common.constants.Constants;

import java.util.Date;

/**
 * Job信息
 *
 */
public class Job implements Constants {

	/** 主键 job的ID */
	private long id;
	/**应用名**/
	private String appName;

	private long appId;
	
	/** 创建时间 */
	private Date gmtCreate;
	
	/** 修改时间 */
	private Date gmtModified;
	
	/** Job描述 */
	private String description;
	
	/** 创建者ID */
	private String createrId;
	
	/** Job类型 */
	private int type;
	
	/** 时间表达式 */
	private String cronExpression;

	
	/** job处理器 */
	private String jobProcessor;
	
	/** 最大运行实例数量 */
	//private int maxInstanceAmount;
	
	/** Job用户自定义参数 */
	private String jobArguments;

	/** Job状态 */
	private int status = JOB_STATUS_ENABLE;
	
	//Job等级
	//private int level;
	
	//最大线程数量
	//private int maxThreads;
	
	/** taskName */
    private String taskName;
	
	/**
	 * 重写equals方法
	 */
	public boolean equals(Object object) {
		if(null == object) {
			return false;
		}
		if(! (object instanceof Job)) {
			return false;
		}
		
		Job job = (Job)object;
		if(! job.toString().equals(this.toString())) {
			return false;
		}
		
		return true;
	}

	/**
	 * 设置触发时间
	 * @param date
	 */
	public void setFireTime(Date date) {
		this.cronExpression = TimeUtil.getCron(date);
	}
	
	/**
	 * 重写hashCode方法
	 */
	public int hashCode() {
		return this.toString().hashCode();
	}
	

    public static Job newInstance(String json) {
        return RemotingSerializable.fromJson(json, Job.class);
    }

    /**
     * 对象转换成json
     */
    @Override
    public String toString() {
        return RemotingSerializable.toJson(this, false);
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreaterId() {
		return createrId;
	}

	public void setCreaterId(String createrId) {
		this.createrId = createrId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public String getJobProcessor() {
		return jobProcessor;
	}

	public void setJobProcessor(String jobProcessor) {
		this.jobProcessor = jobProcessor;
	}

//	public int getMaxInstanceAmount() {
//		return maxInstanceAmount;
//	}
//
//	public void setMaxInstanceAmount(int maxInstanceAmount) {
//		this.maxInstanceAmount = maxInstanceAmount;
//	}

	public String getJobArguments() {
		return jobArguments;
	}

	public void setJobArguments(String jobArguments) {
		this.jobArguments = jobArguments;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

//	public int getLevel() {
//		return level;
//	}
//
//	public void setLevel(int level) {
//		this.level = level;
//	}

//	public int getMaxThreads() {
//		return maxThreads;
//	}
//
//	public void setMaxThreads(int maxThreads) {
//		this.maxThreads = maxThreads;
//	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}
}
