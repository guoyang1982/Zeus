package com.gy.sched.client.remoting.proxy;

import com.alibaba.fastjson.JSON;
import com.gy.sched.common.domain.remoting.protocol.InvokeMethod;
import com.gy.sched.common.util.StringUtil;
import com.gy.sched.client.context.ClientContext;
import com.gy.sched.common.constants.Constants;
import com.gy.sched.common.context.InvocationContext;
import com.gy.sched.common.domain.remoting.RemoteMachine;
import com.gy.sched.common.proxy.ProxyAbstractService;
import com.gy.sched.common.proxy.ProxyService;
import com.gy.sched.common.remoting.protocol.RemotingCommand;
import com.gy.sched.common.remoting.protocol.RemotingSerializable;
import com.gy.sched.common.util.BytesUtil;
import com.gy.sched.common.util.RemotingUtil;
import io.netty.channel.Channel;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by guoyang on 17/4/6.
 */
public class ClientProxyInterceptor extends ProxyAbstractService implements ClientContext, Constants {
    private static final Log logger = LogFactory.getLog(ClientProxyInterceptor.class);
    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        RemoteMachine remoteMachine = InvocationContext.acquireRemoteMachine();
        remoteMachine.setAppName(clientConfig.getAppName());
        remoteMachine.setClientId(clientConfig.getClientId());
        remoteMachine.setLocalVersion(clientConfig.getVersion());
        remoteMachine.setCrashRetry(clientConfig.isCrashRetry());
        Channel channel = clientRemoting.getAndCreateChannel(remoteMachine.getRemoteAddress());
        if (null == channel) {
            logger.error("[ClientProxyInterceptor]: getAndCreateChannel error"
                    + ", server:" + remoteMachine.getRemoteAddress()
                    + ", timeout:" + remoteMachine.getTimeout()
                    + ", methodName:" + method.getName());
            InvocationContext.clean();
            return null;
        }
        remoteMachine.setLocalAddress(RemotingUtil.socketAddress2String(channel.localAddress()));

        Class<?>[] parameterTypesClass = method.getParameterTypes();
        String[] parameterTypesString = new String[parameterTypesClass.length];
        String[] arguments = new String[parameterTypesClass.length];
        for (int i = 0; i < parameterTypesClass.length; i++) {
            parameterTypesString[i] = parameterTypesClass[i].getName();
            arguments[i] = RemotingSerializable.toJson(args[i], false);
        }
        InvokeMethod invokeMethod = new InvokeMethod(remoteMachine, method.getName(), parameterTypesString, arguments, method.getReturnType().getName());
        byte[] requestBody = null;
        try {
            requestBody = BytesUtil.objectToBytes(invokeMethod.toString());
        } catch (Throwable e) {
            logger.error("[ClientProxyInterceptor]: objectToBytes error"
                    + ", server:" + remoteMachine.getRemoteAddress()
                    + ", timeout:" + remoteMachine.getTimeout()
                    + ", methodName:" + method.getName(), e);
            InvocationContext.clean();
            return null;
        }
        if (null == requestBody) {
            logger.error("[ClientProxyInterceptor]: requestBody is null"
                    + ", server:" + remoteMachine.getRemoteAddress()
                    + ", timeout:" + remoteMachine.getTimeout()
                    + ", methodName:" + method.getName());
            InvocationContext.clean();
            return null;
        }
        RemotingCommand request = new RemotingCommand();
        request.setBody(requestBody);
        RemotingCommand response = null;
        try {
            response = clientRemoting.invokeSync(remoteMachine.getRemoteAddress(), request, remoteMachine.getTimeout());
        } catch (Throwable e) {
            logger.error("[ClientProxyInterceptor]: invoke error"
                    + ", server:" + remoteMachine.getRemoteAddress()
                    + ", timeout:" + remoteMachine.getTimeout()
                    + ", methodName:" + method.getName(), e);
        }
        InvocationContext.clean();
        if (null == response) {
            logger.error("[ClientProxyInterceptor]: response is null"
                    + ", server:" + remoteMachine.getRemoteAddress()
                    + ", timeout:" + remoteMachine.getTimeout()
                    + ", methodName:" + method.getName());

            return null;
        }
        Class<?> returnClass = ProxyService.getClass(invokeMethod.getReturnType());
        if (void.class == returnClass) {

            return null;
        }
        byte[] responseBody = response.getBody();
        if (null == responseBody) {
            logger.error("[ClientProxyInterceptor]: responseBody is null"
                    + ", server:" + remoteMachine.getRemoteAddress()
                    + ", timeout:" + remoteMachine.getTimeout()
                    + ", methodName:" + method.getName());

            return null;
        }

        String json = null;
        try {
            json = (String) BytesUtil.bytesToObject(responseBody);
        } catch (Throwable e) {
            logger.error("[ClientProxyInterceptor]: bytesToObject error"
                    + ", server:" + remoteMachine.getRemoteAddress()
                    + ", timeout:" + remoteMachine.getTimeout()
                    + ", methodName:" + method.getName(), e);
        }
        if (StringUtil.isBlank(json)) {
            logger.error("[ClientProxyInterceptor]: json is null"
                    + ", server:" + remoteMachine.getRemoteAddress()
                    + ", timeout:" + remoteMachine.getTimeout()
                    + ", methodName:" + method.getName());

            return null;
        }

        Type returnType = method.getGenericReturnType();
        if (returnType instanceof ParameterizedType) {
            return JSON.parseObject(json, (ParameterizedType) returnType);
        }
        return RemotingSerializable.fromJson(json, returnClass);
    }
}
