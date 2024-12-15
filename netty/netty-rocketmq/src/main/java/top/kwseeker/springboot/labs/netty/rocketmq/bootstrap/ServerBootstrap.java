package top.kwseeker.springboot.labs.netty.rocketmq.bootstrap;

import top.kwseeker.springboot.labs.netty.rocketmq.bootstrap.config.NettyServerConfig;

public class ServerBootstrap {

    public static void main(String[] args) {
        NettyServerConfig config = new NettyServerConfig();

        NettyRemotingServer server = new NettyRemotingServer(config);
        server.start();
    }
}
