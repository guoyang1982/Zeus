package com.gy.sched.client.api.impl;

import com.gy.sched.client.api.SimpleTaskApi;
import com.gy.sched.client.context.ClientContext;
import com.gy.sched.common.context.InvocationContext;
import com.gy.sched.common.domain.remoting.RemoteMachine;
import com.gy.sched.common.domain.result.Result;
import com.gy.sched.common.domain.store.Job;
import com.gy.sched.common.service.ServerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Created by guoyang on 17/4/10.
 */
public class SimpleTaskApiImpl implements SimpleTaskApi, ClientContext {
    private static final Log logger = LogFactory.getLog(SimpleTaskApiImpl.class);

    private ServerService serverService = clientRemoting.proxyInterface(ServerService.class);

    @Override
    public boolean addTask(Job job) {
        //api 创建job
        boolean result = registAndCreateJobs(job);
        return result;
    }

    private boolean registAndCreateJobs(Job job) {
        if (null != job) {
            List<String> servers = clientRemoting.getServerList();
            if (null != servers) {
                for (String server : servers) {
                    InvocationContext.setRemoteMachine(new RemoteMachine(server));
                    Result<Boolean> registAndCreateResult = serverService.registerAndCreateJobs(job);
                    if (null == registAndCreateResult) {
                        logger.error("[SimpleTaskApiImpl]: registAndCreateJobs failed, connectResult is null"
                                + ", machineGroup:" + clientConfig.getAppName() + ", server:" + server + ",job:" + job.toString());
                        return false;
                    }
                    if (registAndCreateResult.getData().booleanValue()) {
                        logger.warn("[SimpleTaskApiImpl]: registAndCreateJobs success"
                                + ", registAndCreateJobs:" + registAndCreateResult.toString()
                                + ", machineGroup:" + clientConfig.getAppName() + ", server:" + server + ",job:" + job.toString());
                        return true;
                    } else {
                        logger.error("[SimpleTaskApiImpl]: registAndCreateJobs failed"
                                + ", registAndCreateJobs:" + registAndCreateResult.toString()
                                + ", machineGroup:" + clientConfig.getAppName() + ", server:" + server + ",job:" + job.toString());
                        return false;
                    }

                }
            }
        }
        return false;
    }
}
