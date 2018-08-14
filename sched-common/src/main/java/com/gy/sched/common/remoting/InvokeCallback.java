package com.gy.sched.common.remoting;

import com.gy.sched.common.remoting.netty.ResponseFuture;

/**
 * 异步调用应答回调接口
 */
public interface InvokeCallback {
    public void operationComplete(final ResponseFuture responseFuture);
}
