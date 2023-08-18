package com.zqf.imx;

import com.zqf.imx.entity.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

@ChannelHandler.Sharable
public class ChatMessageCodec extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, List<Object> list) throws Exception {
        ByteBuf outmessage = ctx.alloc().buffer();
        // 前五个字节作为魔数
        byte[] magicNumber = new byte[]{'Z', 'h', 'u', 'Z', 'i'};
        outmessage.writeBytes(magicNumber);
        // 一个字节作为版本号
        outmessage.writeByte(1);
        // 一个字节表示序列化方式  0：JDK、1：Json、2：ProtoBuf.....
        outmessage.writeByte(0);
        // 一个字节用于表示消息类型
        outmessage.writeByte(message.getMessageType());
        // 四个字节表示消息序号
        outmessage.writeInt(message.getSequenceId());

        // 使用Java-Serializable的方式对消息对象进行序列化
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(message);
        byte[] messageBytes = bos.toByteArray();
        // 使用四个字节描述消息正文的长度
        outmessage.writeInt(messageBytes.length);
        // 将序列化后的消息对象作为消息正文
        outmessage.writeBytes(messageBytes);
        // 将封装好的数据传递给下一个处理器
        list.add(outmessage);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 读取前五个字节得到魔数
        byte[] magicNumber = new byte[5];
        byteBuf.readBytes(magicNumber,0,5);
        // 再读取一个字节得到版本号
        byte version = byteBuf.readByte();
        // 再读取一个字节得到序列化方式
        byte serializableType = byteBuf.readByte();
        // 再读取一个字节得到消息类型
        byte messageType = byteBuf.readByte();
        // 再读取四个字节得到消息序号
        int sequenceId = byteBuf.readInt();
        // 再读取四个字节得到消息正文长度
        int messageLength = byteBuf.readInt();
        // 再根据正文长度读取序列化后的字节正文数据
        byte[] messageBytes = new byte[messageLength];
        byteBuf.readBytes(messageBytes,0,messageLength);
        // 对于读取到的消息正文进行反序列化，最终得到具体的消息对象
        ByteArrayInputStream bis = new ByteArrayInputStream(messageBytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Message message = (Message) ois.readObject();
        // 最终把反序列化得到的消息对象传递给后续的处理器
        list.add(message);
    }
}
