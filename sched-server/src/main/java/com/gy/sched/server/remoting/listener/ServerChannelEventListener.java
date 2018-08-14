package com.gy.sched.server.remoting.listener;

import com.gy.sched.common.remoting.ChannelEventListener;
import com.gy.sched.common.util.RemotingUtil;
import com.gy.sched.server.context.ServerContext;
import com.gy.sched.common.util.RemotingHelper;
import io.netty.channel.Channel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 服务端事件监听器
 *
 */
public class ServerChannelEventListener implements ChannelEventListener, ServerContext {

	private static final Log logger = LogFactory.getLog(ServerChannelEventListener.class);
	
	@Override
	public void onChannelConnect(String remoteAddr, Channel channel) {
		final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(channel);
		logger.info("[ServerChannelEventListener]: onChannelConnect {" + remoteAddress + "}");
	}

	@Override
	public void onChannelClose(String remoteAddr, Channel channel) {
		final String remoteAddress = RemotingUtil.socketAddress2String(channel.remoteAddress());
		serverRemoting.deleteConnection(remoteAddress);
		RemotingUtil.closeChannel(channel);
		logger.error("[ServerChannelEventListener]: onChannelClose {" + remoteAddress + "}");
	}

	@Override
	public void onChannelException(String remoteAddr, Channel channel) {
		final String remoteAddress = RemotingUtil.socketAddress2String(channel.remoteAddress());
		serverRemoting.deleteConnection(remoteAddress);
		RemotingUtil.closeChannel(channel);
		logger.error("[ServerChannelEventListener]: onChannelException {" + remoteAddress + "}");
	}

	@Override
	public void onChannelIdle(String remoteAddr, Channel channel) {
		final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(channel);
		logger.info("[ServerChannelEventListener]: onChannelIdle {" + remoteAddress + "}");
	}

}
