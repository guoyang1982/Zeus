package com.gy.sched.common.remoting;

import com.gy.sched.common.remoting.netty.NettyRequestProcessor;
import com.gy.sched.common.remoting.protocol.RemotingCommand;
import com.gy.sched.common.exception.RemotingSendRequestException;
import com.gy.sched.common.exception.RemotingTimeoutException;
import com.gy.sched.common.exception.RemotingTooMuchRequestException;
import io.netty.channel.Channel;

import java.util.concurrent.ExecutorService;


/**
 * 远程通信，Server接口
 */
public interface RemotingServer extends RemotingService {

    /**
     * 注册请求处理器，ExecutorService必须要对应一个队列大小有限制的阻塞队列，防止OOM
     * 
     * @param requestCode
     * @param processor
     * @param executor
     */
    public void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
                                  final ExecutorService executor);


    public void registerDefaultProcessor(final NettyRequestProcessor processor, final ExecutorService executor);


    /**
     * 服务器绑定的本地端口
     * 
     * @return PORT
     */
    public int localListenPort();


    public RemotingCommand invokeSync(final Channel channel, final RemotingCommand request,
                                      final long timeoutMillis) throws InterruptedException, RemotingSendRequestException,
            RemotingTimeoutException;


    public void invokeAsync(final Channel channel, final RemotingCommand request, final long timeoutMillis,
                            final InvokeCallback invokeCallback) throws InterruptedException,
            RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;


    public void invokeOneway(final Channel channel, final RemotingCommand request, final long timeoutMillis)
            throws InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException,
            RemotingSendRequestException;

}
