package com.gy.sched.common.domain.remoting;

import com.gy.sched.common.constants.Constants;
import io.netty.channel.Channel;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 远端机器信息
 *
 */
public class RemoteMachine implements Constants {

	/** 应用名 */
	private String appName;
	
	/** 客户端ID */
	private String clientId;
	
	/** 本地地址 */
	private String localAddress;
	
	/** 本地版本信息 */
	private String localVersion;
	
	/** 信道 不序列化传送到通信对端 由通信对端重新赋值 */
	private transient Channel channel;
	
	/** 远端地址 */
	private String remoteAddress;
	
	/** 远端版本信息 */
	private String remoteVersion;
	
	/** 调用超时时间 */
	private long timeout = DEFAULT_INVOKE_TIMEOUT;
	
	/** 宕机重试 */
	private boolean crashRetry = false;
	
	//运行线程数量
	private AtomicInteger runThreads = new AtomicInteger(0);

	public RemoteMachine() {
		
	}
	
	public RemoteMachine(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	
	public RemoteMachine(String remoteAddress, long timeout) {
		this.remoteAddress = remoteAddress;
		this.timeout = timeout;
	}
	
	public RemoteMachine(String appName, String localAddress, String localVersion, String remoteAddress) {
		this.appName = appName;
		this.localAddress = localAddress;
		this.localVersion = localVersion;
		this.remoteAddress = remoteAddress;
	}
	
	/**
	 * 信息反转
	 */
	public void reversal() {
		String localAddress = this.localAddress;
		String localVersion = this.localVersion;
		this.localAddress = this.remoteAddress;
		this.localVersion = this.remoteVersion;
		this.remoteAddress = localAddress;
		this.remoteVersion = localVersion;
	}
	
	/**
	 * 重写equals方法
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RemoteMachine other = (RemoteMachine) obj;
		if (appName == null) {
			if (other.appName != null)
				return false;
		} else if (!appName.equals(other.appName))
			return false;
		if (localAddress == null) {
			if (other.localAddress != null)
				return false;
		} else if (!localAddress.equals(other.localAddress))
			return false;
		if (remoteAddress == null) {
			if (other.remoteAddress != null)
				return false;
		} else if (!remoteAddress.equals(other.remoteAddress))
			return false;
		return true;
	}
	
	/**
	 * 重写hashCode方法
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appName == null) ? 0 : appName.hashCode());
		result = prime * result
				+ ((localAddress == null) ? 0 : localAddress.hashCode());
		result = prime * result
				+ ((remoteAddress == null) ? 0 : remoteAddress.hashCode());
		return result;
	}
	
	/**
	 * 重写toString方法
	 */
	@Override
	public String toString() {
		return "RemoteMachine [appName=" + appName + ", clientId=" + clientId
				+ ", localAddress=" + localAddress + ", localVersion="
				+ localVersion + ", remoteAddress=" + remoteAddress
				+ ", remoteVersion=" + remoteVersion + ", timeout=" + timeout
				+ ", crashRetry=" + crashRetry + "]";
	}
	
	public String toShortString() {
		return "[appName=" + appName + ", clientId=" + clientId
				+ ", remoteAddress=" + remoteAddress
				+ ", remoteVersion=" + remoteVersion 
				+ ", crashRetry=" + crashRetry + "]";
	}



	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}

	public String getLocalVersion() {
		return localVersion;
	}

	public void setLocalVersion(String localVersion) {
		this.localVersion = localVersion;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public String getRemoteVersion() {
		return remoteVersion;
	}

	public void setRemoteVersion(String remoteVersion) {
		this.remoteVersion = remoteVersion;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public boolean isCrashRetry() {
		return crashRetry;
	}

	public void setCrashRetry(boolean crashRetry) {
		this.crashRetry = crashRetry;
	}

	public AtomicInteger getRunThreads() {
		return runThreads;
	}

	public void setRunThreads(AtomicInteger runThreads) {
		this.runThreads = runThreads;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
}
