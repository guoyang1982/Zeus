package com.gy.sched.common.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by guoyang on 17/4/7.
 */
public abstract class ProxyAbstractService implements ProxyInterface {
    private Enhancer enhancer = new Enhancer();
    public Object getProxy(Class clazz) {
        //设置需要创建子类的类
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        //通过字节码技术动态创建子类实例
        return enhancer.create();
    }
    @Override
    public abstract Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable;
}
