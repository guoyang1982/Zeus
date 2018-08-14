package com.gy.sched.server.remoting.proxy;

import com.alibaba.fastjson.JSON;
import com.gy.sched.common.constants.Constants;
import com.gy.sched.common.context.InvocationContext;
import com.gy.sched.common.domain.remoting.protocol.InvokeMethod;
import com.gy.sched.common.proxy.ProxyAbstractService;
import com.gy.sched.common.proxy.ProxyService;
import com.gy.sched.common.remoting.protocol.RemotingCommand;
import com.gy.sched.common.remoting.protocol.RemotingSerializable;
import com.gy.sched.server.context.ServerContext;
import com.gy.sched.common.domain.remoting.RemoteMachine;
import com.gy.sched.common.util.BytesUtil;
import jodd.util.StringUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by guoyang on 17/4/6.
 */
public class ServerProxyInterceptor extends ProxyAbstractService implements ServerContext, Constants {
    private static final Log logger = LogFactory.getLog(ServerProxyInterceptor.class);

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        RemoteMachine remoteMachine = InvocationContext.acquireRemoteMachine();
        remoteMachine.setLocalAddress(serverConfig.getLocalAddress());

        Class<?>[] parameterTypesClass = method.getParameterTypes();
        String[] parameterTypesString = new String[parameterTypesClass.length];
        String[] arguments = new String[parameterTypesClass.length];
        for(int i = 0 ; i < parameterTypesClass.length ; i ++) {
            parameterTypesString[i] = parameterTypesClass[i].getName();
            arguments[i] = RemotingSerializable.toJson(args[i], false);
        }
        InvokeMethod invokeMethod = new InvokeMethod(remoteMachine, method.getName(), parameterTypesString, arguments, method.getReturnType().getName());
        byte[] requestBody = null;
        try {
            requestBody = BytesUtil.objectToBytes(invokeMethod.toString());
        } catch (Throwable e) {
            logger.error("[ServerProxyInterceptor]: objectToBytes error"
                    + ", remoteMachine:" + remoteMachine.toString()
                    + ", timeout:" + remoteMachine.getTimeout()
                    + ", methodName:" + method.getName(), e);
            InvocationContext.clean();
            return null;
        }
        if(null == requestBody) {
            logger.error("[ServerProxyInterceptor]: requestBody is null"
                    + ", remoteMachine:" + remoteMachine.toString()
                    + ", timeout:" + remoteMachine.getTimeout()
                    + ", methodName:" + method.getName());
            InvocationContext.clean();
            return null;
        }

        RemotingCommand request = new RemotingCommand();
        request.setBody(requestBody);
        RemotingCommand response = null;
        try {
            response = serverRemoting.invokeSync(remoteMachine.getChannel(), request, remoteMachine.getTimeout());
        } catch (Throwable e) {
            logger.error("[ServerProxyInterceptor]: invoke error"
                    + ", remoteMachine:" + remoteMachine.toString()
                    + ", timeout:" + remoteMachine.getTimeout()
                    + ", methodName:" + method.getName(), e);
        }
        InvocationContext.clean();
        if(null == response) {
            logger.error("[ServerProxyInterceptor]: response is null"
                    + ", remoteMachine:" + remoteMachine.toString()
                    + ", timeout:" + remoteMachine.getTimeout()
                    + ", methodName:" + method.getName());
            return null;
        }
        Class<?> returnClass = ProxyService.getClass(invokeMethod.getReturnType());
        if(void.class == returnClass) {
            return null;
        }
        byte[] responseBody = response.getBody();
        if(null == responseBody) {
            logger.error("[ServerProxyInterceptor]: responseBody is null"
                    + ", remoteMachine:" + remoteMachine.toString()
                    + ", timeout:" + remoteMachine.getTimeout()
                    + ", methodName:" + method.getName());
            return null;
        }

        String json = null;
        try {
            json = (String) BytesUtil.bytesToObject(responseBody);
        } catch (Throwable e) {
            logger.error("[ServerProxyInterceptor]: bytesToObject error"
                    + ", remoteMachine:" + remoteMachine.toString()
                    + ", timeout:" + remoteMachine.getTimeout()
                    + ", methodName:" + method.getName(), e);
        }
        if(StringUtil.isBlank(json)) {
            logger.error("[ServerProxyInterceptor]: json is null"
                    + ", remoteMachine:" + remoteMachine.toString()
                    + ", timeout:" + remoteMachine.getTimeout()
                    + ", methodName:" + method.getName());
            return null;
        }

        Type returnType = method.getGenericReturnType();
        if(returnType instanceof ParameterizedType) {
            return JSON.parseObject(json, (ParameterizedType) returnType);
        }
        return RemotingSerializable.fromJson(json, returnClass);
    }
}
