//package top.kwseeker.springboot.labs.netty.rocketmq.server.handler;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.ByteToMessageDecoder;
//import io.netty.handler.codec.ProtocolDetectionResult;
//import io.netty.handler.codec.ProtocolDetectionState;
//import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
//import io.netty.handler.codec.haproxy.HAProxyProtocolVersion;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import top.kwseeker.springboot.labs.netty.rocketmq.common.constant.LoggerName;
//
//import java.util.List;
//import java.util.NoSuchElementException;
//
//public class HandshakeHandler extends ByteToMessageDecoder {
//
//    private static final Logger log = LoggerFactory.getLogger(LoggerName.ROCKETMQ_REMOTING_NAME);
//
//    public HandshakeHandler() {
//    }
//
//    @Override
//    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
//        try {
//            ProtocolDetectionResult<HAProxyProtocolVersion> detectionResult = HAProxyMessageDecoder.detectProtocol(byteBuf);
//            if (detectionResult.state() == ProtocolDetectionState.NEEDS_MORE_DATA) {
//                return;
//            }
//            if (detectionResult.state() == ProtocolDetectionState.DETECTED) {
//                ctx.pipeline().addAfter(defaultEventExecutorGroup, ctx.name(), HA_PROXY_DECODER, new HAProxyMessageDecoder())
//                        .addAfter(defaultEventExecutorGroup, HA_PROXY_DECODER, HA_PROXY_HANDLER, new HAProxyMessageHandler())
//                        .addAfter(defaultEventExecutorGroup, HA_PROXY_HANDLER, TLS_MODE_HANDLER, tlsModeHandler);
//            } else {
//                ctx.pipeline().addAfter(defaultEventExecutorGroup, ctx.name(), TLS_MODE_HANDLER, tlsModeHandler);
//            }
//
//            try {
//                // Remove this handler
//                ctx.pipeline().remove(this);
//            } catch (NoSuchElementException e) {
//                log.error("Error while removing HandshakeHandler", e);
//            }
//        } catch (Exception e) {
//            log.error("process proxy protocol negotiator failed.", e);
//            throw e;
//        }
//    }
//}