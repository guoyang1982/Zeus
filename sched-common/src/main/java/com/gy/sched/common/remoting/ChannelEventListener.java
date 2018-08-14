package com.gy.sched.common.remoting;

import io.netty.channel.Channel;

/**
 * 监听Channel的事件，包括连接断开、连接建立、连接异常，传送这些事件到应用层
 * Created by guoyang on 17/3/17.
 */
public interface ChannelEventListener {
    public void onChannelConnect(final String remoteAddr, final Channel channel);


    public void onChannelClose(final String remoteAddr, final Channel channel);


    public void onChannelException(final String remoteAddr, final Channel channel);


    public void onChannelIdle(final String remoteAddr, final Channel channel);
}
