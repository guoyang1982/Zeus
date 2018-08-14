package com.gy.sched.common.context;

import com.gy.sched.common.domain.remoting.RemoteMachine;

/**
 * 调用上下文
 *
 */
public class InvocationContext {

	/** RemoteMachine的ThreadLocal */
	private static ThreadLocal<RemoteMachine> remoteMachineThreadLocal = new ThreadLocal<RemoteMachine>();
	
	/**
	 * 设置RemoteMachine
	 * @param remoteMachine
	 */
	public static void setRemoteMachine(RemoteMachine remoteMachine) {
		remoteMachineThreadLocal.set(remoteMachine);
	}

	/**
	 * 获取RemoteMachine
	 * @return
	 */
	public static RemoteMachine acquireRemoteMachine() {
		RemoteMachine remoteMachine = remoteMachineThreadLocal.get();
		if(null == remoteMachine) {
            //throw new DtsException("Remote machine is null, acquire should not happen before set.");
		}
		return remoteMachine;
	}

	/**
	 * 清除上下文
	 */
	public static void clean() {
		remoteMachineThreadLocal.remove();
	}
	
}
