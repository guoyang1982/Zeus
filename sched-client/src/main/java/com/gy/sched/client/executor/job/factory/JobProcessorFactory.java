package com.gy.sched.client.executor.job.factory;

import com.google.common.collect.Lists;
import com.gy.sched.client.context.ClientContext;
import com.gy.sched.client.executor.job.processor.SimpleJobProcessor;
import com.gy.sched.common.domain.store.Job;
import com.gy.sched.common.util.StringUtil;
import com.gy.sched.client.annotation.Scheduled;
import com.gy.sched.common.constants.Constants;
import com.gy.sched.common.context.InvocationContext;
import com.gy.sched.common.domain.remoting.RemoteMachine;
import com.gy.sched.common.domain.result.Result;
import com.gy.sched.common.exception.InitException;
import com.gy.sched.common.service.ServerService;
import com.gy.sched.common.util.CommonUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Job处理器工厂
 *
 * @author tianyao.myc
 */
public class JobProcessorFactory implements ClientContext, Constants {

    private static final Log logger = LogFactory.getLog(JobProcessorFactory.class);

    /**
     * spring bean上下文
     */
    private ApplicationContext applicationContext = null;

    /**
     * SimpleJobProcessor缓存
     */
    private ConcurrentHashMap<String, ConcurrentHashMap<String, SimpleJobProcessor>> simpleJobProcessorCache =
            new ConcurrentHashMap<String, ConcurrentHashMap<String, SimpleJobProcessor>>();

    private ServerService serverService = clientRemoting.proxyInterface(ServerService.class);

    private List<Job> jobs = Lists.newArrayList();

    /**
     * 初始化
     *
     * @throws InitException
     */
    public void init() throws InitException {

        /** Spring环境初始化 */
        initSimpleJobProcessorCache4Spring();
        /**注册并创建job**/
        registAndCreateJobs();
    }

    private void registAndCreateJobs(){
        if(jobs.size()>0){
            List<String> servers = clientRemoting.getServerList();
            if(null != servers){
                for(String server : servers){
                    InvocationContext.setRemoteMachine(new RemoteMachine(server));
                    for(Job job:jobs){
                        Result<Boolean> registAndCreateResult = serverService.registerAndCreateJobs(job);
                        if(null == registAndCreateResult) {
                            logger.error("[JobProcessorFactory]: registAndCreateJobs failed, connectResult is null"
                                    + ", machineGroup:" + clientConfig.getAppName() + ", server:" + server + ",job:"+job.toString());
                            return ;
                        }
                        if(registAndCreateResult.getData().booleanValue()) {
                            logger.warn("[JobProcessorFactory]: registAndCreateJobs success"
                                    + ", registAndCreateJobs:" + registAndCreateResult.toString()
                                    + ", machineGroup:" + clientConfig.getAppName() + ", server:" + server + ",job:"+job.toString());
                        } else {
                            logger.error("[JobProcessorFactory]: registAndCreateJobs failed"
                                    + ", registAndCreateJobs:" + registAndCreateResult.toString()
                                    + ", machineGroup:" + clientConfig.getAppName() + ", server:" + server + ",job:"+job.toString());
                        }
                    }
                }
            }
        }
    }


    /**
     * 初始化SimpleJobProcessor缓存
     */
    @SuppressWarnings("rawtypes")
    private void initSimpleJobProcessorCache4Spring() {
        Map<String, SimpleJobProcessor> beanMap = applicationContext.getBeansOfType(SimpleJobProcessor.class);
        if (null == beanMap || beanMap.isEmpty()) {
            logger.warn("[JobProcessorFactory]: initSimpleJobProcessorCache beanMap is empty");
            return;
        }
        Iterator iterator = beanMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String beanId = (String) entry.getKey();
            SimpleJobProcessor simpleJobProcessor = (SimpleJobProcessor) entry.getValue();

            String jobProcessor = simpleJobProcessor.getClass().getName();
            if (AopUtils.isAopProxy(simpleJobProcessor)
                    || AopUtils.isCglibProxy(simpleJobProcessor)
                    || AopUtils.isJdkDynamicProxy(simpleJobProcessor)) {
                jobProcessor = AopUtils.getTargetClass(simpleJobProcessor).getName();
            }
            ConcurrentHashMap<String, SimpleJobProcessor> simpleJobProcessorMap = this.simpleJobProcessorCache.get(jobProcessor);
            if (null == simpleJobProcessorMap) {
                simpleJobProcessorMap = new ConcurrentHashMap<String, SimpleJobProcessor>();
                this.simpleJobProcessorCache.put(jobProcessor, simpleJobProcessorMap);
            }
            simpleJobProcessorMap.put(beanId, simpleJobProcessor);

            //获取对象上的方法注解
            Method[] methods = simpleJobProcessor.getClass().getMethods();
            for(Method m:methods){
                if(m.getName().equals("process")){
                    Scheduled sched = m.getAnnotation(Scheduled.class);
                    if(null != sched){
                        Job job = new Job();
                        job.setAppName(clientConfig.getAppName());
                        job.setCronExpression(sched.cron());;
                        job.setDescription(sched.description());
                        job.setJobProcessor(jobProcessor);
                        jobs.add(job);
                    }
                }
            }
            logger.warn("[JobProcessorFactory]: initSimpleJobProcessorCache jobProcessor:" + jobProcessor + ", beanId:" + beanId);
        }
    }

    /**
     * 接口检查
     *
     * @param types
     * @param job
     */
    private void checkInterface(Type[] types, Job job) {
        boolean simple = false;
        boolean parallel = false;
        for (Type type : types) {
            if (type.equals(SimpleJobProcessor.class)) {
                simple = true;
            }
//			if(type.equals(ParallelJobProcessor.class)) {
//				parallel = true;
//			}
        }
        if (CommonUtil.isSimpleJob(job.getType())) {
            if (simple && parallel) {
//				throw new RuntimeException("[JobProcessorFactory]: your choice is simple job"
//						+ ", can not implements both " + SimpleJobProcessor.class.getName()
//						+ " and " + ParallelJobProcessor.class.getName()
//						+ ", please check:" + job.getJobProcessor());
            } else if (simple && !parallel) {

            } else if (!simple && parallel) {
//				throw new RuntimeException("[JobProcessorFactory]: your choice is simple job"
//						+ ", but implements " + ParallelJobProcessor.class.getName()
//						+ ", please check:" + job.getJobProcessor());
            } else {
                throw new RuntimeException("[JobProcessorFactory]: your choice is simple job"
                        + ", but not implements " + SimpleJobProcessor.class.getName()
                        + ", please check:" + job.getJobProcessor());
            }
        } else {
            if (simple && parallel) {
//				throw new RuntimeException("[JobProcessorFactory]: your choice is parallel job"
//						+ ", can not implements both " + SimpleJobProcessor.class.getName()
//						+ " and " + ParallelJobProcessor.class.getName()
//						+ ", please check:" + job.getJobProcessor());
            } else if (simple && !parallel) {
                throw new RuntimeException("[JobProcessorFactory]: your choice is parallel job"
                        + ", but implements " + SimpleJobProcessor.class.getName()
                        + ", please check:" + job.getJobProcessor());
            } else if (!simple && parallel) {

            } else {
//				throw new RuntimeException("[JobProcessorFactory]: your choice is parallel job"
//						+ ", but not implements " + ParallelJobProcessor.class.getName()
//						+ ", please check:" + job.getJobProcessor());
            }
        }
    }

    /**
     * 初始化SpringJobProcessor
     *
     * @param job
     * @param jobProcessor
     */
    private void initSpringJobProcessor(Job job, Object jobProcessor) {
        String[] jobProcessorProperties = job.getJobProcessor().split(COLON);

        /** 填充字段 */
        Field[] fields = jobProcessor.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            String fieldName = fields[i].getName();
            Object object = null;
            try {
                object = applicationContext.getBean(fieldName);
            } catch (Throwable e) {
                logger.warn("[JobProcessorFactory]: initSpringJobProcessor field not found"
                        + ", jobProcessor:" + jobProcessorProperties[0].trim()
                        + ", fieldName:" + fieldName);
            }
            if (object != null) {
                try {
                    fields[i].set(jobProcessor, object);
                } catch (Throwable e) {
                    logger.error("[JobProcessorFactory]: initSpringJobProcessor field set error"
                            + ", jobProcessor:" + jobProcessorProperties[0].trim()
                            + ", fieldName:" + fieldName
                            + ", object:" + object, e);
                    continue;
                }
                logger.warn("[JobProcessorFactory]: initSpringJobProcessor set field"
                        + ", jobProcessor:" + jobProcessorProperties[0].trim()
                        + ", fieldName:" + fieldName);
            }
        }

        /** 调用初始化方法 */
        if (jobProcessorProperties.length >= 2) {
            String initMethod = jobProcessorProperties[1].trim();
            if (StringUtil.isNotBlank(initMethod)) {
                Method method = null;
                try {
                    method = jobProcessor.getClass().getDeclaredMethod(initMethod);
                } catch (Throwable e) {
                    logger.error("[JobProcessorFactory]: initSpringJobProcessor getDeclaredMethod error"
                            + ", jobProcessor:" + jobProcessorProperties[0].trim()
                            + ", initMethod:" + initMethod, e);
                }
                if (null == method) {
                    logger.error("[JobProcessorFactory]: initSpringJobProcessor getDeclaredMethod failed"
                            + ", jobProcessor:" + jobProcessorProperties[0].trim()
                            + ", initMethod:" + initMethod);
                } else {
                    try {
                        method.invoke(jobProcessor);
                    } catch (Throwable e) {
                        logger.error("[JobProcessorFactory]: initSpringJobProcessor invoke initMethod error"
                                + ", jobProcessor:" + jobProcessorProperties[0].trim()
                                + ", initMethod:" + initMethod, e);
                    }
                }
            } else {
                logger.error("[JobProcessorFactory]: initSpringJobProcessor initMethod is null"
                        + ", jobProcessor:" + jobProcessorProperties[0].trim()
                        + ", initMethod:" + initMethod);
            }
        }
    }

    /**
     * 创建并获取SimpleJobProcessor
     * @param job
     * @param isCheck
     * @return
     */
    public SimpleJobProcessor createAndGetSimpleJobProcessor(Job job, boolean isCheck) {
        String[] jobProcessorProperties = job.getJobProcessor().split(COLON);
        String jobProcessor = jobProcessorProperties[POSITION_PROCESSOR].trim();

        ConcurrentHashMap<String, SimpleJobProcessor> simpleJobProcessorMap = this.simpleJobProcessorCache.get(jobProcessor);
        if(null == simpleJobProcessorMap && ! clientConfig.isEveryTimeNew()) {
            simpleJobProcessorMap = new ConcurrentHashMap<String, SimpleJobProcessor>();
            this.simpleJobProcessorCache.put(jobProcessor, simpleJobProcessorMap);
        }

        SimpleJobProcessor simpleJobProcessor = null;

        if(! CollectionUtils.isEmpty(simpleJobProcessorMap) && ! clientConfig.isEveryTimeNew()) {

            if(1 == simpleJobProcessorMap.size()) {
                simpleJobProcessor = (SimpleJobProcessor)simpleJobProcessorMap.values().toArray()[0];
                return simpleJobProcessor;
            }

            if(jobProcessorProperties.length < 3) {
                simpleJobProcessor = simpleJobProcessorMap.get(jobProcessor);
                if(simpleJobProcessor != null) {
                    return simpleJobProcessor;
                }
                throw new RuntimeException("[JobProcessorFactory]: you have more than one jobProcessor instance for " + job.getJobProcessor()
                        + ", but you do not fill beanId on console. please check job config, jobId:" + job.getId()
                        + "! you should fill the config item like this 'com.xxx.app.JobProcessor::beanId'.");
            } else {
                String beanId = jobProcessorProperties[POSITION_BEAN_ID].trim();
                if(StringUtil.isNotBlank(beanId)) {
                    simpleJobProcessor = simpleJobProcessorMap.get(beanId);
                    if(simpleJobProcessor != null) {
                        return simpleJobProcessor;
                    }
                }
                throw new RuntimeException("[JobProcessorFactory]: you have more than one jobProcessor instance for " + job.getJobProcessor()
                        + ". you maybe fill wrong beanId on console. please check job config, jobId:" + job.getId()
                        + "! DtsClient can not find bean:" + beanId);
            }
        }

        Object object = proxyService.newInstance(jobProcessor);
        if(null == object) {
            throw new RuntimeException("[JobProcessorFactory]: can not create a new simple job processor, please check:" + job.getJobProcessor());
        }

        Type[] types = proxyService.aquireInterface(object);
        if(0 == types.length) {
            throw new RuntimeException("[JobProcessorFactory]: your choice is simple job"
                    + ", but not implements " + SimpleJobProcessor.class.getName()
                    + ", please check:" + job.getJobProcessor());
        }

        checkInterface(types, job);

        simpleJobProcessor = (SimpleJobProcessor)object;

        if(clientConfig.isSpring()) {
            initSpringJobProcessor(job, simpleJobProcessor);
        }

        if(! isCheck && ! clientConfig.isEveryTimeNew()) {
            simpleJobProcessorMap.put(jobProcessor, simpleJobProcessor);
        }
        return simpleJobProcessor;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
