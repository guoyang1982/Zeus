package com.gy.sched.common.remoting.netty;

import com.gy.sched.common.remoting.protocol.RemotingCommand;
import com.gy.sched.common.util.RemotingHelper;
import com.gy.sched.common.util.RemotingUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.ByteBuffer;


/**
 * 协议编码器
 *
 */
public class NettyEncoder extends MessageToByteEncoder<RemotingCommand> {
	
    private static final Log log = LogFactory.getLog(NettyEncoder.class);

    @Override
    public void encode(ChannelHandlerContext ctx, RemotingCommand remotingCommand, ByteBuf out)
            throws Exception {
        try {
        	 ByteBuffer header = remotingCommand.encodeHeader();
             out.writeBytes(header);
            byte[] body = remotingCommand.getBody();
            if (body != null) {
                out.writeBytes(body);
            }
        }
        catch (Throwable e) {
            log.error("encode exception, " + RemotingHelper.parseChannelRemoteAddr(ctx.channel()), e);
            if (remotingCommand != null) {
                log.error(remotingCommand.toString());
            }
            // 这里关闭后， 会在pipeline中产生事件，通过具体的close事件来清理数据结构
            RemotingUtil.closeChannel(ctx.channel());
        }
    }
}
