package com.gy.sched.common.remoting;

public interface RemotingService {
    public void start();


    public void shutdown();


    public void registerRPCHook(RPCHook rpcHook);
}
