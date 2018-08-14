package com.gy.sched.common.proxy;

import net.sf.cglib.proxy.MethodInterceptor;

/**
 * Created by guoyang on 17/4/6.
 */
public interface ProxyInterface extends MethodInterceptor {
    public Object getProxy(Class clazz);
}
