package top.kwseeker.springboot.labs.netty.rocketmq.remoting.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.springboot.labs.netty.rocketmq.common.RemotingHelper;
import top.kwseeker.springboot.labs.netty.rocketmq.protocol.RemotingCommand;
import top.kwseeker.springboot.labs.netty.rocketmq.common.constant.LoggerName;

/**
 * RocketMQ 的自定义编码器
 *
 */
@ChannelHandler.Sharable
public class NettyEncoder extends MessageToByteEncoder<RemotingCommand> {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.ROCKETMQ_REMOTING_NAME);

    @Override
    protected void encode(ChannelHandlerContext ctx, RemotingCommand remotingCommand, ByteBuf out) throws Exception {
        try {
            remotingCommand.fastEncodeHeader(out);  //写头部8字节+header数据
            byte[] body = remotingCommand.getBody();
            if (body != null) {
                log.info("NettyEncoder#encode(), cmd body: {}", new String(body));
                out.writeBytes(body);               //写body数据
            }
        } catch (Exception e) {
            log.error("encode exception, " + RemotingHelper.parseChannelRemoteAddr(ctx.channel()), e);
            if (remotingCommand != null) {
                log.error(remotingCommand.toString());
            }
            RemotingHelper.closeChannel(ctx.channel());
        }
    }
}
