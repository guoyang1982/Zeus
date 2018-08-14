
package com.gy.sched.common.remoting.netty;

import com.gy.sched.common.remoting.protocol.RemotingCommand;
import com.gy.sched.common.util.RemotingUtil;
import com.gy.sched.common.util.RemotingHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.ByteBuffer;


/**
 * 协议解码器
 */
public class NettyDecoder extends LengthFieldBasedFrameDecoder {
	
    private static final Log log = LogFactory.getLog(NettyDecoder.class);
    
    private static final int FRAME_MAX_LENGTH = Integer.MAX_VALUE;


    public NettyDecoder() {
        super(FRAME_MAX_LENGTH, 0, 4, 0, 4);
    }


    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = null;
        try {
            frame = (ByteBuf) super.decode(ctx, in);
            if (null == frame) {
                return null;
            }

            ByteBuffer byteBuffer = frame.nioBuffer();

            return RemotingCommand.decode(byteBuffer);
        }
        catch (Throwable e) {
            log.error("decode exception, " + RemotingHelper.parseChannelRemoteAddr(ctx.channel()), e);
            // 这里关闭后， 会在pipeline中产生事件，通过具体的close事件来清理数据结构
            RemotingUtil.closeChannel(ctx.channel());
        }
        finally {
            if (null != frame) {
                frame.release();
            }
        }

        return null;
    }
}
