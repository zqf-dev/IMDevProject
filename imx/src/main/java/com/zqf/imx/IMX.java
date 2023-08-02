package com.zqf.imx;

import com.zqf.imx.utils.IMTools;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class IMX {
    private static final String TAG = IMX.class.getName();
    private static IMX instance = null;
    public static IMX getInstance() {
        if (instance == null) {
            synchronized (IMX.class) {
                if (instance == null) {
                    instance = new IMX();
                }
            }
        }
        return instance;
    }

    //进行初始化
    public void initIMConnect() {
        // 工作线程组, 老板线程组会把任务丢给他，让手下线程组去做任务，服务客户
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap client = new Bootstrap();
            client.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChildChannelHandler());

            // 发起异步连接操作
            ChannelFuture f = client.connect(IMTools.IP, IMTools.PORT).sync();
            // 等待客户端链路关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅退出，释放线程池资源
            group.shutdownGracefully();
        }
    }

    private static class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
            channel.pipeline().addLast(new ClientHandler());
        }
    }
}
