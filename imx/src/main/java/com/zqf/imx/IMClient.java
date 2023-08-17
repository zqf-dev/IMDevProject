package com.zqf.imx;

import android.text.TextUtils;

import com.zqf.imx.utils.IMXConst;
import com.zqf.imx.utils.MBThreadPoolExecutor;

import java.io.BufferedInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class IMClient {
    private static final String TAG = IMClient.class.getSimpleName();
    private static IMClient instance = null;

    public static IMClient getInstance() {
        if (instance == null) {
            synchronized (IMClient.class) {
                if (instance == null) {
                    instance = new IMClient();
                }
            }
        }
        return instance;
    }

    /**
     * socket 连接
     */
    public void initSocketConnect() throws Exception {
        Socket client = new Socket(IMXConst.IP, IMXConst.PORT);
        try {
            BufferedInputStream in = new BufferedInputStream(client.getInputStream());
            PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), "GB18030"));
            String clientString = "客户端发给服务器端的信息";
            out.println(clientString);
            out.flush();
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    //进行初始化
    public void initIMX(String... IP_Port) {
        if (IP_Port.length > 1) {
            String IP = IP_Port[0];
            String Port = IP_Port[1];
            if (!TextUtils.isEmpty(IP)) IMXConst.IP = IP;
            if (!TextUtils.isEmpty(Port)) IMXConst.PORT = Integer.parseInt(Port);
        }
        MBThreadPoolExecutor.runInBackground(() -> {
            // 工作线程组, 老板线程组会把任务丢给他，让手下线程组去做任务，服务客户
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap client = new Bootstrap();
                client.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new ChildChannelHandler());
                // 发起异步连接操作
                ChannelFuture f = client.connect(IMXConst.IP, IMXConst.PORT).sync();
                // 等待客户端链路关闭
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 优雅退出，释放线程池资源
                group.shutdownGracefully();
            }
        });
    }

    private static class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
            channel.pipeline().addLast(new ClientHandler());
        }
    }
}
