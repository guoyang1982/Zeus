package com.gy.sched.client;

import com.gy.sched.client.context.ClientContext;
import com.gy.sched.common.exception.InitException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by guoyang on 17/3/20.
 */
public class SchedClient implements ClientContext, ApplicationContextAware {
    private static final Log logger = LogFactory.getLog(SchedClient.class);

    /**
     * 初始化
     * @throws InitException
     */
    public void init() throws InitException {


        /** 客户端各项参数配置初始化 */
        clientConfig.init();

        /** Zookeeper初始化 */
        zookeeper.init();

        /** 客户端远程通信初始化 */
        clientRemoting.init();

        /** 初始化Job处理器工厂 */
        jobProcessorFactory.init();

    }
    /*
     * @param appName
     */
    public void setAppName(String appName) {
        clientConfig.setAppName(appName);
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        jobProcessorFactory.setApplicationContext(applicationContext);
        clientConfig.setSpring(true);
        logger.warn("[DtsClient]: setApplicationContext over, applicationContext:" + applicationContext);
    }

    public static void main(String args[]){
        SchedClient schedClient = new SchedClient();
        try {
            schedClient.setAppName("0-0-0-69");
            schedClient.init();
        } catch (InitException e) {
            e.printStackTrace();
        }
    }
}
