package top.kwseeker.springboot.labs.netty.rocketmq.remoting.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.springboot.labs.netty.rocketmq.common.RemotingHelper;
import top.kwseeker.springboot.labs.netty.rocketmq.common.constant.LoggerName;
import top.kwseeker.springboot.labs.netty.rocketmq.protocol.RemotingCommand;

public class NettyDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.ROCKETMQ_REMOTING_NAME);

    private static final int FRAME_MAX_LENGTH =
        Integer.parseInt(System.getProperty("com.rocketmq.remoting.frameMaxLength", "16777216"));

    public NettyDecoder() {
        super(FRAME_MAX_LENGTH, 0, 4, 0, 4);
    }

    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = null;
        //Stopwatch timer = Stopwatch.createStarted();
        try {
            // TODO
            frame = (ByteBuf) super.decode(ctx, in);
            if (null == frame) {
                return null;
            }
            // TODO
            RemotingCommand cmd = RemotingCommand.decode(frame);
            log.info("NettyDecoder#decode(), cmd={}", cmd);
            //cmd.setProcessTimer(timer);
            return cmd;
        } catch (Exception e) {
            log.error("decode exception, " + RemotingHelper.parseChannelRemoteAddr(ctx.channel()), e);
            RemotingHelper.closeChannel(ctx.channel());
        } finally {
            if (null != frame) {
                frame.release();
            }
        }

        return null;
    }
}
