package com.gy.sched.server.remoting;

import com.gy.sched.common.remoting.netty.NettyClientConfig;
import com.gy.sched.common.remoting.netty.NettyRemotingClient;

/**
 * Created by guoyang on 17/3/17.
 */
public class ClientRemoting {
    /** 远程通信客户端 */
    private NettyRemotingClient client = null;

    public void init(){

        /** 初始化远程通信客户端 */
        initRemotingClient();
    }

    private void initRemotingClient(){
        NettyClientConfig config = new NettyClientConfig();

        client = new NettyRemotingClient(config);

        try {
            client.start();
        } catch (Throwable e) {
            //throw new InitException("[ClientRemoting]: initRemotingClient error", e);
        }
    }
}
