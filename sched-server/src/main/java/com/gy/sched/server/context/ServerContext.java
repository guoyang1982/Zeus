package com.gy.sched.server.context;

import com.gy.sched.common.proxy.ProxyService;
import com.gy.sched.server.remoting.ServerRemoting;
import com.gy.sched.server.store.mysql.access.DataSource;
import com.gy.sched.server.zookeeper.Zookeeper;
import com.gy.sched.server.config.ServerConfig;
import com.gy.sched.server.job.pool.JobPool;
import com.gy.sched.server.manager.JobInstanceManager;
import com.gy.sched.server.manager.JobManager;
import com.gy.sched.server.manager.TaskSnapShotManager;
import com.gy.sched.server.remoting.ClientRemoting;
import com.gy.sched.server.service.ServerServiceImpl;
import com.gy.sched.server.store.Store;
import com.gy.sched.server.store.mysql.access.SqlMapClients;

/**
 * Created by guoyang on 17/3/17.
 */
public interface ServerContext {

    /** 服务器配置 */
    public static final ServerConfig serverConfig = new ServerConfig();
    /** 代理服务 */
    public static final ProxyService proxyService = new ProxyService();

    /** 客户端远程通信 */
    public static final ClientRemoting clientRemoting = new ClientRemoting();

    /** 远程通信 */
    public static final ServerRemoting serverRemoting = new ServerRemoting();

    /** 服务端通用基础服务 */
    public static final ServerServiceImpl serverService = new ServerServiceImpl();

    /** Zookeeper */
    public static final Zookeeper zookeeper = new Zookeeper();
    /** Job池 */
    public static final JobPool jobPool = new JobPool();

    /** Job管理器 */
    public static final JobManager jobManager = new JobManager();

    public static final JobInstanceManager jobInstanceManager = new JobInstanceManager();

    /** 用户任务执行快照 */
    public static final TaskSnapShotManager taskSnapShotManager = new TaskSnapShotManager();

    /** 数据源 */
    public static final DataSource dataSource = new DataSource();

    /** SqlMapClients */
    public static final SqlMapClients sqlMapClients = new SqlMapClients();
    /** 存储 */
    public static final Store store = new Store();

}
