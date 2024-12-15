package top.kwseeker.springboot.labs.netty.rocketmq.protocol;

import com.alibaba.fastjson.annotation.JSONField;
import io.netty.buffer.ByteBuf;
import top.kwseeker.springboot.labs.netty.rocketmq.common.exception.RemotingCommandException;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据包中数据的类型，
 * 接受到请求后先将数据存入ByteBuf, 然后经过 Decoder 解码得到 RemotingCommand 对象
 * 原类型有很多字段，这里简化了
 * <p>
 * RocketMQ RemotingCommand 的编码协议是：
 *  4字节：记录报文的总长度：4 + header + body 的总长度，不包含报文长度本身的4字节
 *  1字节：记录序列化编码协议类型：0：JSON, 1: ROCKETMQ
 *  3字节：记录头部数据的长度
 *  m字节：存储header数据 (RemotingCommand 对象序列化后的内容)
 *  n字节：存储消息体内容 (RemotingCommand 对象中 body 内容)
 */
public class RemotingCommand {

    // flag 第0位，表示这个命令是请求命令还是响应命令，0 REQUEST_COMMAND 1 RESPONSE_COMMAND
    private static final int RPC_TYPE = 0;
    private static final AtomicInteger requestId = new AtomicInteger(0);

    private int code;
    // 一对请求和响应使用同一个 opaque
    private int opaque = requestId.getAndIncrement();
    // 命令的属性，
    // 第0位，表示这个命令是请求命令还是响应命令
    private int flag = 0;
    // 描述信息
    private String remark;
    // RocketMQ 使用此字段实现消息的分发控制等，这里忽略
    //private SendMessageRequestHeaderV2 customHeader;
    // 消息体内容字节数组，RocketMQ使用body传输消息体
    private transient byte[] body;
    // RemotingCommand 对象被序列化后作为 header 部分，RocketMQ 中支持JSON、ROCKETMQ两种序列化实现，默认使用JSON
    // 这里只展示JSON序列化实现，JSON序列化借助 FastJSON
    private SerializeType serializeTypeCurrentRPC = SerializeType.JSON;

    /**
     * 写数据包头部信息（前8个字节），即报文长度、序列化协议类型、头部数据长度
     * 和 header数据
     */
    public void fastEncodeHeader(ByteBuf out) {
        int bodySize = this.body != null ? this.body.length : 0;
        int beginIndex = out.writerIndex(); //写指针
        out.writeLong(0);
        // 写头部数据，默认使用JSON序列化，将RemotingCommand实例序列化成JSON字节数组
        int headerSize;
        byte[] header = RemotingSerializable.encode(this); //内部借助 FastJSON 序列化
        headerSize = header.length;
        out.writeBytes(header);
        out.setInt(beginIndex, 4 + headerSize + bodySize);  //写前4字节
        out.setInt(beginIndex + 4, markProtocolType(headerSize, serializeTypeCurrentRPC));
    }

    public static int markProtocolType(int source, SerializeType type) {
        return (type.getCode() << 24) | (source & 0x00FFFFFF);
    }

    public void markResponseType() {
        int bits = 1 << RPC_TYPE;
        this.flag |= bits;
    }

    public static RemotingCommand decode(final ByteBuf byteBuffer) throws RemotingCommandException {
        int length = byteBuffer.readableBytes(); //记录头部数据长度的4字节 + header数据 + body数据
        // 读取第二个4字节，即序列化协议类型和头部数据长度
        int oriHeaderLen = byteBuffer.readInt();
        int headerLength = getHeaderLength(oriHeaderLen);   //第2个4字节中后3字节是头部数据长度
        if (headerLength > length - 4) {
            throw new RemotingCommandException("decode error, bad header length: " + headerLength);
        }

        // header 部分反序列化
        RemotingCommand cmd = headerDecode(byteBuffer, headerLength, getProtocolType(oriHeaderLen));

        int bodyLength = length - 4 - headerLength;
        byte[] bodyData = null;
        if (bodyLength > 0) {
            bodyData = new byte[bodyLength];
            byteBuffer.readBytes(bodyData);
        }
        cmd.body = bodyData;

        return cmd;
    }

    public static int getHeaderLength(int length) {
        return length & 0xFFFFFF;
    }

    public static SerializeType getProtocolType(int source) {
        return SerializeType.valueOf((byte) ((source >> 24) & 0xFF));
    }

    private static RemotingCommand headerDecode(ByteBuf byteBuffer, int len,
                                                SerializeType type) throws RemotingCommandException {
        switch (type) {
            case JSON:
                byte[] headerData = new byte[len];
                byteBuffer.readBytes(headerData);
                RemotingCommand resultJson = RemotingSerializable.decode(headerData, RemotingCommand.class);
                //resultJson.setSerializeTypeCurrentRPC(type);
                return resultJson;
            //case ROCKETMQ:
            //    RemotingCommand resultRMQ = RocketMQSerializable.rocketMQProtocolDecode(byteBuffer, len);
            //    resultRMQ.setSerializeTypeCurrentRPC(type);
            //    return resultRMQ;
            default:
                break;
        }

        return null;
    }

    @JSONField(serialize = false)
    public RemotingCommandType getType() {
        if (this.isResponseType()) {
            return RemotingCommandType.RESPONSE_COMMAND;
        }

        return RemotingCommandType.REQUEST_COMMAND;
    }

    @JSONField(serialize = false)
    public boolean isResponseType() {
        int bits = 1 << RPC_TYPE;
        return (this.flag & bits) == bits;
    }

    public static RemotingCommand createResponseCommand(int code, String remark) {
        return createResponseCommand(code, remark, null);
    }

    /**
     * 创建响应对象
     * @param code command id
     * @param classHeader
     * @return
     */
    public static RemotingCommand createResponseCommand(int code, String remark,
                                                        //Class<? extends CommandCustomHeader> classHeader) {
                                                        Class<?> classHeader) {
        RemotingCommand cmd = new RemotingCommand();
        cmd.markResponseType();
        cmd.setCode(code);
        cmd.setRemark(remark);
        //setCmdVersion(cmd);

        //if (classHeader != null) {
        //    try {
        //        CommandCustomHeader objectHeader = classHeader.getDeclaredConstructor().newInstance();
        //        cmd.customHeader = objectHeader;
        //    } catch (InstantiationException e) {
        //        return null;
        //    } catch (IllegalAccessException e) {
        //        return null;
        //    } catch (InvocationTargetException e) {
        //        return null;
        //    } catch (NoSuchMethodException e) {
        //        return null;
        //    }
        //}

        return cmd;
    }

    public static RemotingCommand createRequestCommand(int code) {
        RemotingCommand cmd = new RemotingCommand();
        cmd.setCode(code);
        //cmd.customHeader = customHeader;
        //setCmdVersion(cmd);
        return cmd;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getOpaque() {
        return opaque;
    }

    public void setOpaque(int opaque) {
        this.opaque = opaque;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public SerializeType getSerializeTypeCurrentRPC() {
        return serializeTypeCurrentRPC;
    }

    public void setSerializeTypeCurrentRPC(SerializeType serializeTypeCurrentRPC) {
        this.serializeTypeCurrentRPC = serializeTypeCurrentRPC;
    }

    @Override
    public String toString() {
        return "RemotingCommand{" +
                "code=" + code +
                ", opaque=" + opaque +
                ", flag=" + flag +
                ", remark='" + remark + '\'' +
                ", body=" + Arrays.toString(body) +
                ", serializeTypeCurrentRPC=" + serializeTypeCurrentRPC +
                '}';
    }
}

