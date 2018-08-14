package com.gy.sched.server.job.pool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gy.sched.common.constants.Constants;
import com.gy.sched.common.domain.result.Result;
import com.gy.sched.common.domain.store.App;
import com.gy.sched.common.domain.store.Job;
import com.gy.sched.common.domain.store.JobServerRelation;
import com.gy.sched.common.exception.AccessException;
import com.gy.sched.common.exception.InitException;
import com.gy.sched.server.context.ServerContext;
import com.gy.sched.server.job.InternalJob;
import com.gy.sched.server.job.pool.timer.OperationCheckTimer;
import com.gy.sched.server.store.AppAccess;
import com.gy.sched.server.store.JobAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * Job池
 */
public class JobPool implements ServerContext, Constants {

    private static final Log logger = LogFactory.getLog("jobPool");

    /**
     * ZK操作节点检查线程数量
     */
    public static final int CHECK_ZK_OPERATION_THREAD_AMOUNT = 1;

    /**
     * ZK操作节点检查线程名称
     */
    public static final String CHECK_ZK_OPERATION_THREAD_NAME = "DTS-ZK-operation-check-thread-";

    /**
     * Job映射表
     */
    private ConcurrentHashMap<Long, InternalJob> jobTable = new ConcurrentHashMap<Long, InternalJob>();

    /**
     * Job信息访问接口
     */
    private JobAccess jobAccess;

    /**
     * 应用信息访问接口
     */
    private AppAccess appAccess;

    /**
     * job和机器关系映射访问接口
     */
    //private JobServerRelationAccess jobServerRelationAccess;

    /**
     * 定时程序调度工厂
     */
    private SchedulerFactory schedulerFactory;

    /**
     * 定时调度服务
     */
    private ScheduledExecutorService executorService = Executors
            .newScheduledThreadPool(CHECK_ZK_OPERATION_THREAD_AMOUNT, new ThreadFactory() {

                int index = 0;

                public Thread newThread(Runnable runnable) {
                    index++;
                    return new Thread(runnable, CHECK_ZK_OPERATION_THREAD_NAME + index);
                }

            });

    /**
     * 请求队列
     */
    private LinkedBlockingQueue<Runnable> requestQueue = new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE);

    private ThreadPoolExecutor executors = new ThreadPoolExecutor(5, 5,
            60 * 1000L, TimeUnit.MILLISECONDS,
            requestQueue,
            new ThreadFactory() {

                int index = 0;

                public Thread newThread(Runnable runnable) {

                    index++;

                    return new Thread(runnable, "DTS-operation-thread-" + index);
                }

            });

    /**
     * 初始化
     *
     * @throws InitException
     */
    public void init() throws InitException {

        Properties properties = new Properties();
        properties.setProperty("org.quartz.threadPool.threadCount", "32");
        properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");

        try {
            this.schedulerFactory = new StdSchedulerFactory(properties);
        } catch (Throwable e) {
            throw new InitException("[JobPool]: new StdSchedulerFactory error", e);
        }

        /** 初始化job */
        initJobs();

        /** 初始化操作检查定时器 */
        //initOperationCheckTimer();

    }

    private JobAccess getJobAccess() {
        if (jobAccess == null) {
            jobAccess = store.getJobAccess();
        }
        return jobAccess;
    }

    private AppAccess getAppAccess() {
        if (appAccess == null) {
            appAccess = store.getAppAccess();
        }
        return appAccess;
    }
//
//    private JobServerRelationAccess getJobServerRelationAccess() {
//        if (jobServerRelationAccess == null) {
//            jobServerRelationAccess = store.getJobServerRelationAccess();
//        }
//        return jobServerRelationAccess;
//    }

    /**
     * 初始化job
     *
     * @throws InitException
     */
    private void initJobs() throws InitException {

//        List<JobServerRelation> jobServerRelationList = null;
//        try {
//            jobServerRelationList = loadJobServerRelationList();
//        } catch (Throwable e) {
//            throw new InitException("[JobPool]: initJobs loadJobServerRelationList error, server:" + serverConfig.getLocalAddress(), e);
//        }
//
//        if (CollectionUtils.isEmpty(jobServerRelationList)) {
//            logger.warn("[JobPool]: initJobs JobServerRelationList is empty, server:" + serverConfig.getLocalAddress());
//            return;
//        }

        // for (JobServerRelation jobServerRelation : jobServerRelationList) {

        /** 加载并创建内存Job */
       // boolean loadAndCreateResult = loadAndCreateInternalJob("initJobs", 123);
        Job query = new Job();
        List<Job> jobs = null;
        try {
            jobs = getJobAccess().query(query);
        } catch (AccessException e) {
            e.printStackTrace();
        }
        for(Job job:jobs){
            job.setAppName("lms");

            Result<Boolean> createResult = jobManager.createInternalJob(job);
            if (createResult.getData().booleanValue()) {
                logger.info("[JobPool]: loadAndCreateInternalJob success"
                        + ", createResult:" + createResult.toString()
                        + ", job:" + job.toString()
                        + ", server:" + serverConfig.getLocalAddress());
            } else {
                logger.error("[JobPool]: loadAndCreateInternalJob error"
                        + ", createResult:" + createResult.toString()
                        + ", job:" + job.toString()
                        + ", server:" + serverConfig.getLocalAddress());
            }
        }

        // logger.info("[JobPool]: initJobs loadAndCreateInternalJob, jobServerRelation:" + jobServerRelation + ", loadAndCreateResult:" + loadAndCreateResult);
        //}
    }

    /**
     * 初始化操作节点检查定时器
     *
     * @throws InitException
     */
    private void initOperationCheckTimer() throws InitException {
        try {
            executorService.scheduleAtFixedRate(new OperationCheckTimer(),
                    0L, 5 * 1000L, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            throw new InitException("[JobPool]: initOperationCheckTimer error", e);
        }
    }

    /**
     * 处理各项操作
     */
//    public void handleOperations() {
//    	List<JobOperation> jobOperationList = jobOperationManager.queryByServer(serverConfig.getLocalAddress());
//        if (CollectionUtils.isEmpty(jobOperationList)) {
//            return;
//        }
//        for(JobOperation jobOperation : jobOperationList) {
//        	handleOperation(jobOperation);
//        }
//    }

    /**
     * 处理操作
     * @param jobOperation
     */
//    private void handleOperation(final JobOperation jobOperation) {
//    	OperationContent operationContent = null;
//    	try {
//			operationContent = OperationContent.newInstance(jobOperation.getOperation());
//		} catch (Throwable e) {
//			logger.error("[JobPool]: OperationContent.newInstance error, jobOperation:" + jobOperation, e);
//		}
//    	if(null == operationContent) {
//    		logger.error("[JobPool]: OperationContent.newInstance failed, jobOperation:" + jobOperation);
//    		return ;
//    	}
//        boolean operationResult = false;
//        try {
//            if (JOB_CREATE_OPERATE.equals(operationContent.getOperate())) {
//                operationResult = createJob(jobOperation.getJobId());
//            } else if (JOB_UPDATE_OPERATE.equals(operationContent.getOperate())) {
//                operationResult = updateJob(jobOperation.getJobId());
//            } else if (JOB_DELETE_OPERATE.equals(operationContent.getOperate())) {
//                operationResult = deleteJob(jobOperation.getJobId());
//            } else if (JOB_INSTANCE_START_OPERATE.equals(operationContent.getOperate())) {
//                JSONObject json = JSON.parseObject(operationContent.getValue());
//                Date fireTime = json.getDate(Constants.FIRE_TIME_ITEM);
//                String uniqueId = json.getString(Constants.FIRE_UNIQUE_ID);
//                operationResult = startJob(jobOperation.getJobId(), fireTime, uniqueId);
//            } else if (JOB_INSTANCE_STOP_OPERATE.equals(operationContent.getOperate())) {
//
//            	try {
//					executors.execute(new Runnable() {
//
//						@Override
//						public void run() {
//
//							try {
//								stopJob(jobOperation.getJobId());
//							} catch (Throwable e) {
//								logger.error("[JobPool]: stopJob error, jobOperation:" + jobOperation, e);
//							}
//						}
//
//					});
//				} catch (Throwable e) {
//					logger.error("[JobPool]: execute stopJob error, jobOperation:" + jobOperation, e);
//				}
//
//                operationResult = true;
//            } else if (JOB_ENABLE_OPERATE.equals(operationContent.getOperate())) {
//                operationResult = enableJob(jobOperation.getJobId());
//            } else if (JOB_DISABLE_OPERATE.equals(operationContent.getOperate())) {
//                operationResult = disableJob(jobOperation.getJobId());
//            } else if (DESIGNATED_MACHINE.equals(operationContent.getOperate())) {
//            	operationResult = designatedMachine(jobOperation.getJobId());
//            }
//
//            if (operationResult) {
//                /** 根据ID删除操作记录 */
//            	jobOperationManager.deleteById(jobOperation);
//            }
//        } catch (Throwable e) {
//            logger.error("[JobPool]: do operation error"
//                    + ", jobOperation:" + jobOperation
//                    + ", server:" + serverConfig.getLocalAddress(), e);
//        }
//    }

    /**
     * 创建Job
     *
     * @param jobId
     * @return
     */
    private boolean createJob(long jobId) {
        InternalJob internalJob = this.jobTable.get(jobId);
        if (null == internalJob) {
            /** 加载并创建内存Job */
            return loadAndCreateInternalJob("createJob", jobId);
        }
        return true;
    }

    /**
     * 加载并创建内存Job
     *
     * @param source
     * @param jobId
     * @return
     */
    private boolean loadAndCreateInternalJob(String source, long jobId) {
        Job query = new Job();
        query.setId(jobId);
        Job job = null;
        try {
            job = loadJob(query);
        } catch (Throwable e) {
            logger.error("[JobPool]: loadAndCreateInternalJob loadJob error"
                    + ", query:" + query.toString()
                    + ", source:" + source, e);
            return false;
        }
        if (null == job) {
            logger.error("[JobPool]: loadAndCreateInternalJob loadJob, job is null"
                    + ", query:" + query.toString()
                    + ", source:" + source);
            return true;
        }

        if (JOB_STATUS_ENABLE != job.getStatus()) {
            logger.warn("[JobPool]: loadAndCreateInternalJob, job is disable"
                    + ", job:" + job
                    + ", source:" + source);
            return true;
        }

        Result<Boolean> createResult = jobManager.createInternalJob(job);
        if (createResult.getData().booleanValue()) {
            logger.info("[JobPool]: loadAndCreateInternalJob success"
                    + ", createResult:" + createResult.toString()
                    + ", job:" + job.toString()
                    + ", server:" + serverConfig.getLocalAddress()
                    + ", source:" + source);
            return true;
        } else {
            logger.error("[JobPool]: loadAndCreateInternalJob error"
                    + ", createResult:" + createResult.toString()
                    + ", job:" + job.toString()
                    + ", server:" + serverConfig.getLocalAddress()
                    + ", source:" + source);
            return true;
        }
    }

    /**
     * 创建内存Job
     * @return
     */
    public Result<Boolean> createInternalJob(Job job) {
            //创建job 写入数据库，根据 类名+应用+执行时间 唯一索引
        Result<Boolean> createResult = new Result<>(false);
        try {
            App appPage = new App();
            appPage.setAppName(job.getAppName());
            List<App> apps = getAppAccess().queryAppByName(appPage);
            if(null == apps && apps.size() == 0){
                return createResult;
            }

            job.setAppId(apps.get(0).getId());
            Job jobDB = getJobAccess().queryJobByAppIdAndEx(job);
            if(null != jobDB){
                job.setId(jobDB.getId());
            }else {
                getJobAccess().insert(job);
            }
        } catch (AccessException e) {
            logger.info("重复创建!e={}",e);
        }

        createResult = jobManager.createInternalJob(job);
            if (createResult.getData().booleanValue()) {
                logger.info("[JobPool]: createInternalJob success"
                        + ", createResult:" + createResult.toString()
                        + ", job:" + job.toString()
                        + ", server:" + serverConfig.getLocalAddress());
            } else {
                logger.error("[JobPool]: createInternalJob error"
                        + ", createResult:" + createResult.toString()
                        + ", job:" + job.toString()
                        + ", server:" + serverConfig.getLocalAddress());
            }

        return createResult;
    }


    /**
     * 更新Job
     *
     * @param jobId
     * @return
     */
    private boolean updateJob(long jobId) {
        Job query = new Job();
        query.setId(jobId);
        Job job = null;
        try {
            job = loadJob(query);
        } catch (Throwable e) {
            logger.error("[JobPool]: updateJob loadJob error, query:" + query.toString(), e);
            return false;
        }
        if (null == job) {
            logger.error("[JobPool]: updateJob loadJob, job is null, query:" + query.toString());
            return true;
        }

        InternalJob internalJob = this.jobTable.get(jobId);
        if (null == internalJob) {
            logger.warn("[JobPool]: updateJob internalJob is null, job:" + job);
            /** 加载并创建内存Job */
            return loadAndCreateInternalJob("updateJob", jobId);
        }

        if (job.equals(internalJob.getJob())) {
            logger.warn("[JobPool]: updateJob ,job not change, job:" + job + ", internalJob:" + internalJob.getJob());
            return true;
        }

        Result<Boolean> updateResult = jobManager.updateInternalJob(job);
        if (updateResult.getData().booleanValue()) {
            logger.info("[JobPool]: updateJob success"
                    + ", updateResult:" + updateResult.toString()
                    + ", job:" + job.toString() + ", server:" + serverConfig.getLocalAddress());
            return true;
        } else {
            logger.error("[JobPool]: updateJob error"
                    + ", updateResult:" + updateResult.toString()
                    + ", job:" + job.toString() + ", server:" + serverConfig.getLocalAddress());
            return true;
        }
    }

    /**
     * 删除Job
     *
     * @param jobId
     * @return
     */
    private boolean deleteJob(long jobId) {
        Job query = new Job();
        query.setId(jobId);
        Job job = null;
        try {
            job = loadJob(query);
        } catch (Throwable e) {
            logger.error("[JobPool]: deleteJob loadJob error, query:" + query.toString(), e);
            return false;
        }
        if (job != null) {
            logger.error("[JobPool]: deleteJob job exists, job:" + job);
            return true;
        }

        InternalJob internalJob = this.jobTable.get(jobId);
        if (null == internalJob) {
            logger.warn("[JobPool]: deleteJob job not exists, jobId:" + jobId);
            return true;
        }

        Result<Boolean> deleteResult = jobManager.deleteInternalJob(query);
        if (deleteResult.getData().booleanValue()) {
            logger.info("[JobPool]: deleteJob success"
                    + ", deleteResult:" + deleteResult.toString()
                    + ", job:" + query.toString() + ", server:" + serverConfig.getLocalAddress());
            return true;
        } else {
            logger.error("[JobPool]: deleteJob error"
                    + ", deleteResult:" + deleteResult.toString()
                    + ", job:" + query.toString() + ", server:" + serverConfig.getLocalAddress());
            return true;
        }
    }

    /**
     * 触发Job
     * @param jobId
     * @param fireTime
     * @param uniqueId
     * @return
     */
//    private boolean startJob(long jobId, Date fireTime, String uniqueId) {
//        Job query = new Job();
//        query.setId(jobId);
//        Job job = null;
//        try {
//            job = loadJob(query);
//        } catch (Throwable e) {
//            logger.error("[JobPool]: loadJob error, query:" + query.toString(), e);
//            return false;
//        }
//        if (null == job) {
//            logger.error("[JobPool]: job is null, job:" + query);
//            return true;
//        }
//        // 判断是否有依赖Job,有依赖Job不能立即执行;
//        JobRelation jobRelation = new JobRelation();
//        jobRelation.setJobId(job.getId());
//        Result<Boolean> checkResult = jobRelationManager.checkAllBeforeDone(jobRelation);
//        if(!checkResult.getData()) {
//            return true;
//        }
//
//        List<String> machineList = new ArrayList<String>();
//
//        Result<Boolean> fireResult = jobManager.fireJob(job, fireTime, uniqueId, machineList);
//        if (fireResult.getData().booleanValue()) {
//            logger.info("[JobPool]: startJob success"
//                    + ", fireResult:" + fireResult.toString()
//                    + ", job:" + job + ", server:" + serverConfig.getLocalAddress()
//                    + ", machineList:" + machineList);
//        } else {
//            logger.error("[JobPool]: startJob error"
//                    + ", fireResult:" + fireResult.toString()
//                    + ", job:" + job + ", server:" + serverConfig.getLocalAddress()
//                    + ", machineList:" + machineList);
//        }
//        return true;
//    }

    /**
     * 停止Job
     * @param jobId
     * @return
     */
//    private boolean stopJob(long jobId) {
//        Job query = new Job();
//        query.setId(jobId);
//        Job job = null;
//        try {
//            job = loadJob(query);
//        } catch (Throwable e) {
//            logger.error("[JobPool]: stopJob loadJob error, query:" + query.toString(), e);
//            return false;
//        }
//        if(null == job) {
//            logger.error("[JobPool]: stopJob job is null, job:" + query);
//            return true;
//        }
//
//        boolean result = jobInstanceManager.finishAllJobInstance(job, INVOKE_SOURCE_API);
//
//        logger.info("[JobPool]: stopJob , result:" + result + ", jobId:" + jobId);
//
//        return true;
//    }

    /**
     * 启用Job
     *
     * @param jobId
     * @return
     */
    private boolean enableJob(long jobId) {
        Job query = new Job();
        query.setId(jobId);
        Job job = null;
        try {
            job = loadJob(query);
        } catch (Throwable e) {
            logger.error("[JobPool]: enableJob loadJob error, query:" + query.toString(), e);
            return false;
        }
        if (null == job) {
            logger.error("[JobPool]: enableJob loadJob, job is null, query:" + query.toString());
            return true;
        }

        if (JOB_STATUS_ENABLE != job.getStatus()) {
            logger.warn("[JobPool]: enableJob loadJob, job disable, job:" + job);
            return true;
        }

        boolean createResult = createJob(jobId);
        if (createResult) {
            logger.info("[JobPool]: enableJob success"
                    + ", createResult:" + createResult
                    + ", job:" + job.toString() + ", server:" + serverConfig.getLocalAddress());
            return true;
        } else {
            logger.error("[JobPool]: enableJob error"
                    + ", createResult:" + createResult
                    + ", job:" + job.toString() + ", server:" + serverConfig.getLocalAddress());
            return true;
        }
    }

    /**
     * 禁用Job
     *
     * @param jobId
     * @return
     */
    private boolean disableJob(long jobId) {
        Job query = new Job();
        query.setId(jobId);
        Job job = null;
        try {
            job = loadJob(query);
        } catch (Throwable e) {
            logger.error("[JobPool]: disableJob loadJob error, query:" + query.toString(), e);
            return false;
        }
        if (null == job) {
            logger.error("[JobPool]: disableJob loadJob, job is null, query:" + query.toString());
            return true;
        }

        if (JOB_STATUS_DISABLE != job.getStatus()) {
            logger.warn("[JobPool]: disableJob loadJob, job enable, job:" + job);
            return true;
        }

        InternalJob internalJob = this.jobTable.get(jobId);
        if (null == internalJob) {
            logger.warn("[JobPool]: disableJob job not exists, jobId:" + jobId);
            return true;
        }

        Result<Boolean> deleteResult = jobManager.deleteInternalJob(query);
        if (deleteResult.getData().booleanValue()) {
            logger.info("[JobPool]: disableJob success"
                    + ", deleteResult:" + deleteResult.toString()
                    + ", job:" + query.toString() + ", server:" + serverConfig.getLocalAddress());
            return true;
        } else {
            logger.error("[JobPool]: disableJob error"
                    + ", deleteResult:" + deleteResult.toString()
                    + ", job:" + query.toString() + ", server:" + serverConfig.getLocalAddress());
            return true;
        }

    }

    /**
     * 指定机器
     * @param jobId
     * @return
     */
//    private boolean designatedMachine(long jobId) {
//    	Job query = new Job();
//        query.setId(jobId);
//        Job job = null;
//        try {
//            job = loadJob(query);
//        } catch (Throwable e) {
//            logger.error("[JobPool]: designatedMachine loadJob error, query:" + query.toString(), e);
//            return false;
//        }
//        if (null == job) {
//            logger.error("[JobPool]: designatedMachine loadJob, job is null, query:" + query.toString());
//            return true;
//        }
//
//        String groupId = GroupIdUtil.generateGroupId(serverConfig.getClusterId(),
//				serverConfig.getServerGroupId(), serverConfig.getJobBackupAmount(), job.getClientGroupId());
//
//        ConcurrentHashMap<String, ConcurrentHashMap<Long, List<DesignatedMachine>>> designatedMachinesTable = serverRemoting.getDesignatedMachinesTable();
//        ConcurrentHashMap<Long, List<DesignatedMachine>> designatedMachinesMap = designatedMachinesTable.get(groupId);
//
//    	List<DesignatedMachine> designatedMachineList = loadDesignatedMachineList(jobId);
//    	if(CollectionUtils.isEmpty(designatedMachineList)) {
//    		if(null == designatedMachinesMap || designatedMachinesMap.isEmpty()) {
//    			serverRemoting.refreshAvailableDesignatedMachine(groupId, jobId);
//    			return true;
//    		}
//    		designatedMachinesMap.remove(jobId);
//    		serverRemoting.refreshAvailableDesignatedMachine(groupId, jobId);
//    		return true;
//    	}
//
//    	if(null == designatedMachinesMap) {
//    		designatedMachinesMap = new ConcurrentHashMap<Long, List<DesignatedMachine>>();
//			designatedMachinesTable.put(groupId, designatedMachinesMap);
//    	}
//
//    	designatedMachinesMap.put(jobId, designatedMachineList);
//
//    	serverRemoting.refreshAvailableDesignatedMachine(groupId, jobId);
//    	return true;
//    }
//

    /**
     * 放入内部Job
     *
     * @param job
     * @param internalJob
     * @throws Throwable
     */
    public void put(Job job, InternalJob internalJob) throws Throwable {
        try {
            jobTable.put(job.getId(), internalJob);
        } catch (Throwable e) {
            throw new RuntimeException("[JobPool]: put error, internalJob:" + internalJob.toString(), e);
        }
    }

    /**
     * 获取内部Job
     *
     * @param job
     * @return
     */
    public InternalJob get(Job job) {
        try {
            return jobTable.get(job.getId());
        } catch (Throwable e) {
            logger.error("[JobPool]: get error, job:" + job.toString(), e);
        }
        return null;
    }

    /**
     * 删除内部Job
     *
     * @param job
     * @throws Throwable
     */
    public void remove(Job job) throws Throwable {
        try {
            jobTable.remove(job.getId());
        } catch (Throwable e) {
            throw new RuntimeException("[JobPool]: remove error, job:" + job.toString(), e);
        }
    }

    /**
     * 加载Job
     *
     * @param query
     * @return
     */
    public Job loadJob(Job query) throws AccessException {
        Job job = null;
        try {
            //job = getJobAccess().queryJobById(query);
            job = new Job();
            job.setId(123l);
            job.setAppName("lms");
            job.setJobProcessor("com.letv.sched.example.ExampleA");
            job.setCronExpression("0 0/2 * * * ?");
        } catch (Throwable e) {
            throw new AccessException("[JobPool]: loadJob error, query:" + query.toString(), e);
        }
        return job;
    }

    /**
     * 加载Job列表
     *
     * @param query
     * @return
     */
    public List<Job> loadJobList(Job query) {
        List<Job> jobList = null;
        try {
            jobList = getJobAccess().query(query);
        } catch (Throwable e) {
            logger.error("[JobPool]: loadJobList error, query:" + query.toString(), e);
        }
        return jobList;
    }

    /**
     * 加载Job和Server关系列表
     *
     * @return
     */
    public List<JobServerRelation> loadJobServerRelationList() throws AccessException {
        JobServerRelation query = new JobServerRelation();
        query.setServer(serverConfig.getLocalAddress());
        List<JobServerRelation> JobServerRelationList = null;
        try {
            //JobServerRelationList = getJobServerRelationAccess().query(query);
        } catch (Throwable e) {
            throw new AccessException("[JobPool]: loadJobServerRelationList error, query:" + query.toString(), e);
        }
        return JobServerRelationList;
    }

//    /**
//     * 加载指定机器列表
//     * @param jobId
//     * @return
//     */
//    private List<DesignatedMachine> loadDesignatedMachineList(long jobId) {
//    	DesignatedMachine query = new DesignatedMachine();
//    	query.setJobId(jobId);
//    	List<DesignatedMachine> designatedMachineList = null;
//    	try {
//			designatedMachineList = store.getDesignatedMachineAccess().queryByJobId(query);
//		} catch (Throwable e) {
//			logger.error("[JobPool]: loadDesignatedMachineList error, query:" + query.toString(), e);
//		}
//    	return designatedMachineList;
//    }

    public SchedulerFactory getSchedulerFactory() {
        return schedulerFactory;
    }

}
