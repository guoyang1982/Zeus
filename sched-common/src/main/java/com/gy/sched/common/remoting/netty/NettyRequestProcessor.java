package com.gy.sched.common.remoting.netty;

import com.gy.sched.common.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by guoyang on 17/3/17.
 */
public interface NettyRequestProcessor {
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request)
            throws Exception;
}
