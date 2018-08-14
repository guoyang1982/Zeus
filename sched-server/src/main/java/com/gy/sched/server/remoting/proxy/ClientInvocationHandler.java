//package com.letv.sched.server.remoting.proxy;
//
//import com.alibaba.fastjson.JSON;
//import com.letv.sched.common.constants.Constants;
//import com.letv.sched.common.context.InvocationContext;
//import com.letv.sched.common.domain.remoting.RemoteMachine;
//import com.letv.sched.common.domain.remoting.protocol.InvokeMethod;
//import com.letv.sched.common.proxy.ProxyService;
//import com.letv.sched.common.remoting.protocol.RemotingCommand;
//import com.letv.sched.common.remoting.protocol.RemotingSerializable;
//import com.letv.sched.common.util.BytesUtil;
//import com.letv.sched.server.context.ServerContext;
//import jodd.util.StringUtil;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Method;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//
///**
// * 客户端代理调用接口
// *
// */
//public class ClientInvocationHandler implements InvocationHandler, ServerContext, Constants {
//
//	private static final Log logger = LogFactory.getLog(ClientInvocationHandler.class);
//
//	/**
//	 * 拦截方法调用各项参数
//	 */
//	public Object invoke(Object proxy, Method method, Object[] args)
//			throws Throwable {
//
//		RemoteMachine remoteMachine = InvocationContext.acquireRemoteMachine();
//		remoteMachine.setLocalAddress(serverConfig.getLocalAddress());
//
//		Class<?>[] parameterTypesClass = method.getParameterTypes();
//		String[] parameterTypesString = new String[parameterTypesClass.length];
//		String[] arguments = new String[parameterTypesClass.length];
//		for(int i = 0 ; i < parameterTypesClass.length ; i ++) {
//			parameterTypesString[i] = parameterTypesClass[i].getName();
//			arguments[i] = RemotingSerializable.toJson(args[i], false);
//		}
//		InvokeMethod invokeMethod = new InvokeMethod(remoteMachine, method.getName(), parameterTypesString, arguments, method.getReturnType().getName());
//		byte[] requestBody = null;
//		try {
//			requestBody = BytesUtil.objectToBytes(invokeMethod.toString());
//		} catch (Throwable e) {
//			logger.error("[ClientInvocationHandler]: objectToBytes error"
//					+ ", serverIp:" + remoteMachine.getRemoteAddress()
//					+ ", timeout:" + remoteMachine.getTimeout()
//					+ ", methodName:" + method.getName(), e);
//            InvocationContext.clean();
//			return null;
//		}
//		if(null == requestBody) {
//			logger.error("[ClientInvocationHandler]: requestBody is null"
//					+ ", serverIp:" + remoteMachine.getRemoteAddress()
//					+ ", timeout:" + remoteMachine.getTimeout()
//					+ ", methodName:" + method.getName());
//            InvocationContext.clean();
//			return null;
//		}
//		RemotingCommand request = new RemotingCommand();
//		request.setBody(requestBody);
//		RemotingCommand response = null;
//		try {
//			response = clientRemoting.invokeSync(remoteMachine.getRemoteAddress(), request, remoteMachine.getTimeout());
//		} catch (Throwable e) {
//			logger.error("[ClientInvocationHandler]: invoke error"
//					+ ", serverIp:" + remoteMachine.getRemoteAddress()
//					+ ", timeout:" + remoteMachine.getTimeout()
//					+ ", methodName:" + method.getName(), e);
//		}
//        InvocationContext.clean();
//		if(null == response) {
//			logger.error("[ClientInvocationHandler]: response is null"
//					+ ", serverIp:" + remoteMachine.getRemoteAddress()
//					+ ", timeout:" + remoteMachine.getTimeout()
//					+ ", methodName:" + method.getName());
//
//			return null;
//		}
//		Class<?> returnClass = ProxyService.getClass(invokeMethod.getReturnType());
//		if(void.class == returnClass) {
//
//			return null;
//		}
//		byte[] responseBody = response.getBody();
//		if(null == responseBody) {
//			logger.error("[ClientInvocationHandler]: responseBody is null"
//					+ ", serverIp:" + remoteMachine.getRemoteAddress()
//					+ ", timeout:" + remoteMachine.getTimeout()
//					+ ", methodName:" + method.getName());
//
//			return null;
//		}
//
//		String json = null;
//		try {
//			json = (String)BytesUtil.bytesToObject(responseBody);
//		} catch (Throwable e) {
//			logger.error("[ClientInvocationHandler]: bytesToObject error"
//					+ ", serverIp:" + remoteMachine.getRemoteAddress()
//					+ ", timeout:" + remoteMachine.getTimeout()
//					+ ", methodName:" + method.getName(), e);
//		}
//		if(StringUtil.isBlank(json)) {
//			logger.error("[ClientInvocationHandler]: json is null"
//					+ ", serverIp:" + remoteMachine.getRemoteAddress()
//					+ ", timeout:" + remoteMachine.getTimeout()
//					+ ", methodName:" + method.getName());
//
//			return null;
//		}
//
//		Type returnType = method.getGenericReturnType();
//		if(returnType instanceof ParameterizedType) {
//			return JSON.parseObject(json, (ParameterizedType)returnType);
//		}
//		return RemotingSerializable.fromJson(json, returnClass);
//	}
//
//}
