package com.gy.sched.server;

import com.gy.sched.server.context.ServerContext;
import com.gy.sched.common.exception.InitException;

/**
 * Created by guoyang on 17/3/17.
 */
public class SchedServer implements ServerContext {

    public void init() {


        try {
            /** 服务器配置初始化 */
            serverConfig.init();
            /** 客户端远程通信 */
            //clientRemoting.init();

            /** 初始化数据源 */
            dataSource.init();
            /** SqlMapClients初始化 */
            sqlMapClients.init();
            /** 数据存储 */
            store.init();
            /** 服务端远程通信初始化 */
            serverRemoting.init();
            /** Zookeeper初始化 */
            zookeeper.init();
            /** Job池初始化 */
            jobPool.init();
        } catch (InitException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        /** 设置INI配置文件路径 */
        String path = Thread.currentThread().getContextClassLoader()
                .getResource("").getPath();


        path = path.substring(0, path.length())
                + "/sched.ini";

        serverConfig.setConfigPath(path);

       SchedServer schedServer = new SchedServer();
        schedServer.init();


    }

}
