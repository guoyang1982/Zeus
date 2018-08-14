package com.gy.sched.server.store;

import com.gy.sched.server.context.ServerContext;
import com.gy.sched.server.store.mysql.AppAccess4Mysql;
import com.gy.sched.server.store.mysql.JobAccess4Mysql;
import com.gy.sched.server.store.mysql.JobInstanceSnapshotAccess4Mysql;
import com.gy.sched.common.constants.Constants;
import com.gy.sched.common.exception.InitException;
import com.gy.sched.server.store.mysql.TaskSnapshotAccess4Mysql;

/**
 * 存储
 */
public class Store implements ServerContext, Constants {


    /**
     * Job信息访问接口
     */
    private JobAccess jobAccess;
    /**
     * Job实例快照访问接口
     */
    private JobInstanceSnapshotAccess jobInstanceSnapshotAccess;
    /**
     * 任务快照访问接口
     */
    private TaskSnapshotAccess taskSnapshotAccess;
    private AppAccess appAccess;

    /**
     * 初始化
     *
     * @throws InitException
     */
    public void init() throws InitException {

        this.jobAccess = new JobAccess4Mysql();
        this.jobInstanceSnapshotAccess = new JobInstanceSnapshotAccess4Mysql();
        this.taskSnapshotAccess = new TaskSnapshotAccess4Mysql();
        this.appAccess = new AppAccess4Mysql();
    }


    public JobAccess getJobAccess() {
        return jobAccess;
    }

    public JobInstanceSnapshotAccess getJobInstanceSnapshotAccess() {
        return jobInstanceSnapshotAccess;
    }

    public TaskSnapshotAccess getTaskSnapshotAccess() {
        return taskSnapshotAccess;
    }

    public AppAccess getAppAccess() {
        return appAccess;
    }
}
