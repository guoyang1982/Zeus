
package com.gy.sched.common.remoting;

import com.gy.sched.common.exception.RemotingConnectException;
import com.gy.sched.common.exception.RemotingSendRequestException;
import com.gy.sched.common.exception.RemotingTimeoutException;
import com.gy.sched.common.remoting.netty.NettyRequestProcessor;
import com.gy.sched.common.remoting.protocol.RemotingCommand;
import com.gy.sched.common.exception.RemotingTooMuchRequestException;

import java.util.List;
import java.util.concurrent.ExecutorService;


/**
 * 远程通信，Client接口
 */
public interface RemotingClient extends RemotingService {

    public void updateNameServerAddressList(final List<String> addrs);


    public List<String> getNameServerAddressList();


    public RemotingCommand invokeSync(final String addr, final RemotingCommand request,
                                      final long timeoutMillis) throws InterruptedException, RemotingConnectException,
            RemotingSendRequestException, RemotingTimeoutException;


    public void invokeAsync(final String addr, final RemotingCommand request, final long timeoutMillis,
                            final InvokeCallback invokeCallback) throws InterruptedException, RemotingConnectException,
            RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;


    public void invokeOneway(final String addr, final RemotingCommand request, final long timeoutMillis)
            throws InterruptedException, RemotingConnectException, RemotingTooMuchRequestException,
            RemotingTimeoutException, RemotingSendRequestException;


    public void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
                                  final ExecutorService executor);
}
