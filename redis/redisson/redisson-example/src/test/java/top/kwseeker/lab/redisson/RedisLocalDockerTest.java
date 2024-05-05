package top.kwseeker.lab.redisson;

import org.junit.jupiter.api.BeforeEach;
import org.redisson.Redisson;
import org.redisson.api.NatMapper;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.Protocol;
import org.redisson.misc.RedisURI;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 连接本地Redis服务节点，代替每次启动测试都重建Redis容器
 */
public class RedisLocalDockerTest {

    protected static final String NOTIFY_KEYSPACE_EVENTS = "--notify-keyspace-events";

    //protected static final GenericContainer<?> REDIS = createRedis();
    protected static final int SingleServerPort = 6379;

    protected static final Protocol protocol = Protocol.RESP2;

    //单机模式客户端连接
    protected static RedissonClient redisson;
    //集群模式客户端连接
    protected static RedissonClient redissonCluster;

    //private static GenericContainer<?> REDIS_CLUSTER;

    //protected static GenericContainer<?> createRedisWithVersion(String version, String... params) {
    //    return new GenericContainer<>(version)
    //            .withCreateContainerCmdModifier(cmd -> {
    //                List<String> args = new ArrayList<>();
    //                args.add("redis-server");
    //                args.addAll(Arrays.asList(params));
    //                cmd.withCmd(args);
    //            })
    //            .withExposedPorts(6379);
    //}
    //
    //protected static GenericContainer<?> createRedis(String... params) {
    //    return createRedisWithVersion("redis:latest", params);
    //}

    static {
        //REDIS.start();
        Config config = createConfig();
        redisson = Redisson.create(config);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            redisson.shutdown();
            //REDIS.stop();
            if (redissonCluster != null) {
                redissonCluster.shutdown();
                redissonCluster = null;
            }
            //if (REDIS_CLUSTER != null) {
            //    REDIS_CLUSTER.stop();
            //    REDIS_CLUSTER = null;
            //}
        }));
    }

    protected static Config createConfig() {
        //return createConfig(REDIS);
        Config config = new Config();
        config.setProtocol(protocol);
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:" + SingleServerPort);
        return config;
    }

    //protected static Config createConfig(GenericContainer<?> container) {
    //    Config config = new Config();
    //    config.setProtocol(protocol);
    //    config.useSingleServer()
    //            .setAddress("redis://127.0.0.1:" + container.getFirstMappedPort());
    //    return config;
    //}

    public static RedissonClient createInstance() {
        Config config = createConfig();
        return Redisson.create(config);
    }

    /**
     * Redis单机模式测试
     */
    protected void testWithParams(Consumer<RedissonClient> redissonCallback, String... params) {
        //GenericContainer<?> redis = createRedis(params);
        //redis.start();

        Config config = new Config();
        config.setProtocol(protocol);
        config.useSingleServer().setAddress("redis://127.0.0.1:" + SingleServerPort);
        RedissonClient redisson = Redisson.create(config);

        try {
            redissonCallback.accept(redisson);
        } finally {
            redisson.shutdown();
            //redis.stop();
        }
    }

    /**
     * Redis集群模式测试
     */
    protected static void testInCluster(Consumer<RedissonClient> redissonCallback) {
        if (redissonCluster == null) {
            //REDIS_CLUSTER = new GenericContainer<>("vishnunair/docker-redis-cluster")
            //        .withExposedPorts(6379, 6380, 6381, 6382, 6383, 6384)
            //        .withStartupCheckStrategy(new MinimumDurationRunningStartupCheckStrategy(Duration.ofSeconds(15)));
            //REDIS_CLUSTER.start();

            Config config = new Config();
            config.setProtocol(protocol);
            config.useClusterServers()
                    //.setNatMapper(new NatMapper() {
                    //    @Override
                    //    public RedisURI map(RedisURI uri) {
                    //        if (REDIS_CLUSTER.getMappedPort(uri.getPort()) == null) {
                    //            return uri;
                    //        }
                    //        return new RedisURI(uri.getScheme(), REDIS_CLUSTER.getHost(), REDIS_CLUSTER.getMappedPort(uri.getPort()));
                    //    }
                    //})
                    .addNodeAddress("redis://127.0.0.1:26379")
                    .addNodeAddress("redis://127.0.0.1:26380")
                    .addNodeAddress("redis://127.0.0.1:26381")
                    .addNodeAddress("redis://127.0.0.1:26382")
                    .addNodeAddress("redis://127.0.0.1:26383")
                    .addNodeAddress("redis://127.0.0.1:26384");
            redissonCluster = Redisson.create(config);
        }

        redissonCallback.accept(redissonCluster);
    }

    @BeforeEach
    public void beforeEach() {
        redisson.getKeys().flushall();
        if (redissonCluster != null) {
            redissonCluster.getKeys().flushall();
        }
    }

    /**
     * Redis哨兵模式测试
     */
    //protected void withSentinel(BiConsumer<List<GenericContainer<?>>, Config> callback, int slaves) throws InterruptedException {
    //    Network network = Network.newNetwork();
    //
    //    List<GenericContainer<? extends GenericContainer<?>>> nodes = new ArrayList<>();
    //
    //    GenericContainer<?> master =
    //            new GenericContainer<>("bitnami/redis:7.2.4")
    //                    .withNetwork(network)
    //                    .withEnv("REDIS_REPLICATION_MODE", "master")
    //                    .withEnv("ALLOW_EMPTY_PASSWORD", "yes")
    //                    .withNetworkAliases("redis")
    //                    .withExposedPorts(6379);
    //    master.start();
    //    assert master.getNetwork() == network;
    //    int masterPort = master.getFirstMappedPort();
    //    master.withCreateContainerCmdModifier(cmd -> {
    //        cmd.getHostConfig().withPortBindings(
    //                new PortBinding(Ports.Binding.bindPort(Integer.valueOf(masterPort)),
    //                        cmd.getExposedPorts()[0]));
    //    });
    //    nodes.add(master);
    //
    //    for (int i = 0; i < slaves; i++) {
    //        GenericContainer<?> slave =
    //                new GenericContainer<>("bitnami/redis:7.2.4")
    //                        .withNetwork(network)
    //                        .withEnv("REDIS_REPLICATION_MODE", "slave")
    //                        .withEnv("REDIS_MASTER_HOST", "redis")
    //                        .withEnv("ALLOW_EMPTY_PASSWORD", "yes")
    //                        .withNetworkAliases("slave" + i)
    //                        .withExposedPorts(6379);
    //        slave.start();
    //        int slavePort = slave.getFirstMappedPort();
    //        slave.withCreateContainerCmdModifier(cmd -> {
    //            cmd.getHostConfig().withPortBindings(
    //                    new PortBinding(Ports.Binding.bindPort(Integer.valueOf(slavePort)),
    //                            cmd.getExposedPorts()[0]));
    //        });
    //        nodes.add(slave);
    //    }
    //
    //    GenericContainer<?> sentinel1 =
    //            new GenericContainer<>("bitnami/redis-sentinel:7.2.4")
    //
    //                    .withNetwork(network)
    //                    .withEnv("REDIS_SENTINEL_DOWN_AFTER_MILLISECONDS", "5000")
    //                    .withEnv("REDIS_SENTINEL_FAILOVER_TIMEOUT", "10000")
    //                    .withNetworkAliases("sentinel1")
    //                    .withExposedPorts(26379);
    //    sentinel1.start();
    //    int sentinel1Port = sentinel1.getFirstMappedPort();
    //    sentinel1.withCreateContainerCmdModifier(cmd -> {
    //        cmd.getHostConfig().withPortBindings(
    //                new PortBinding(Ports.Binding.bindPort(Integer.valueOf(sentinel1Port)),
    //                        cmd.getExposedPorts()[0]));
    //    });
    //    nodes.add(sentinel1);
    //
    //    GenericContainer<?> sentinel2 =
    //            new GenericContainer<>("bitnami/redis-sentinel:7.2.4")
    //                    .withNetwork(network)
    //                    .withEnv("REDIS_SENTINEL_DOWN_AFTER_MILLISECONDS", "5000")
    //                    .withEnv("REDIS_SENTINEL_FAILOVER_TIMEOUT", "10000")
    //                    .withNetworkAliases("sentinel2")
    //                    .withExposedPorts(26379);
    //    sentinel2.start();
    //    int sentinel2Port = sentinel2.getFirstMappedPort();
    //    sentinel2.withCreateContainerCmdModifier(cmd -> {
    //        cmd.getHostConfig().withPortBindings(
    //                new PortBinding(Ports.Binding.bindPort(Integer.valueOf(sentinel2Port)),
    //                        cmd.getExposedPorts()[0]));
    //    });
    //    nodes.add(sentinel2);
    //
    //    GenericContainer<?> sentinel3 =
    //            new GenericContainer<>("bitnami/redis-sentinel:7.2.4")
    //                    .withNetwork(network)
    //                    .withEnv("REDIS_SENTINEL_DOWN_AFTER_MILLISECONDS", "5000")
    //                    .withEnv("REDIS_SENTINEL_FAILOVER_TIMEOUT", "10000")
    //                    .withNetworkAliases("sentinel3")
    //                    .withExposedPorts(26379);
    //    sentinel3.start();
    //    int sentinel3Port = sentinel3.getFirstMappedPort();
    //    sentinel3.withCreateContainerCmdModifier(cmd -> {
    //        cmd.getHostConfig().withPortBindings(
    //                new PortBinding(Ports.Binding.bindPort(Integer.valueOf(sentinel3Port)),
    //                        cmd.getExposedPorts()[0]));
    //    });
    //    nodes.add(sentinel3);
    //
    //    Thread.sleep(5000);
    //
    //    Config config = new Config();
    //    config.setProtocol(protocol);
    //    config.useSentinelServers()
    //            .setPingConnectionInterval(0)
    //            .setNatMapper(new NatMapper() {
    //
    //                @Override
    //                public RedisURI map(RedisURI uri) {
    //                    for (GenericContainer<? extends GenericContainer<?>> node : nodes) {
    //                        if (node.getContainerInfo() == null) {
    //                            continue;
    //                        }
    //
    //                        Ports.Binding[] mappedPort = node.getContainerInfo().getNetworkSettings()
    //                                .getPorts().getBindings().get(new ExposedPort(uri.getPort()));
    //
    //                        Map<String, ContainerNetwork> ss = node.getContainerInfo().getNetworkSettings().getNetworks();
    //                        ContainerNetwork s = ss.values().iterator().next();
    //
    //                        if (uri.getPort() == 6379
    //                                && !uri.getHost().equals("redis")
    //                                && RedisDockerTest.this.getClass() == RedissonTopicTest.class
    //                                && node.getNetworkAliases().contains("slave0")) {
    //                            return new RedisURI(uri.getScheme(), "127.0.0.1", Integer.valueOf(mappedPort[0].getHostPortSpec()));
    //                        }
    //
    //                        if (mappedPort != null
    //                                && s.getIpAddress().equals(uri.getHost())) {
    //                            return new RedisURI(uri.getScheme(), "127.0.0.1", Integer.valueOf(mappedPort[0].getHostPortSpec()));
    //                        }
    //                    }
    //                    return uri;
    //                }
    //            })
    //            .addSentinelAddress("redis://127.0.0.1:" + sentinel1.getFirstMappedPort())
    //            .setMasterName("mymaster");
    //
    //    callback.accept(nodes, config);
    //
    //    nodes.forEach(n -> n.stop());
    //    network.close();
    //}
    //
    //protected void withSentinel(BiConsumer<List<GenericContainer<?>>, Config> callback, int slaves, String password) throws InterruptedException {
    //    Network network = Network.newNetwork();
    //
    //    List<GenericContainer<? extends GenericContainer<?>>> nodes = new ArrayList<>();
    //
    //    GenericContainer<?> master =
    //            new GenericContainer<>("bitnami/redis:7.2.4")
    //                    .withNetwork(network)
    //                    .withEnv("REDIS_REPLICATION_MODE", "master")
    //                    .withEnv("REDIS_PASSWORD", password)
    //                    .withNetworkAliases("redis")
    //                    .withExposedPorts(6379);
    //    master.start();
    //    assert master.getNetwork() == network;
    //    int masterPort = master.getFirstMappedPort();
    //    master.withCreateContainerCmdModifier(cmd -> {
    //        cmd.getHostConfig().withPortBindings(
    //                new PortBinding(Ports.Binding.bindPort(Integer.valueOf(masterPort)),
    //                        cmd.getExposedPorts()[0]));
    //    });
    //    nodes.add(master);
    //
    //    for (int i = 0; i < slaves; i++) {
    //        GenericContainer<?> slave =
    //                new GenericContainer<>("bitnami/redis:7.2.4")
    //                        .withNetwork(network)
    //                        .withEnv("REDIS_REPLICATION_MODE", "slave")
    //                        .withEnv("REDIS_MASTER_HOST", "redis")
    //                        .withEnv("REDIS_PASSWORD", password)
    //                        .withEnv("REDIS_MASTER_PASSWORD", password)
    //                        .withNetworkAliases("slave" + i)
    //                        .withExposedPorts(6379);
    //        slave.start();
    //        int slavePort = slave.getFirstMappedPort();
    //        slave.withCreateContainerCmdModifier(cmd -> {
    //            cmd.getHostConfig().withPortBindings(
    //                    new PortBinding(Ports.Binding.bindPort(Integer.valueOf(slavePort)),
    //                            cmd.getExposedPorts()[0]));
    //        });
    //        nodes.add(slave);
    //    }
    //
    //    GenericContainer<?> sentinel1 =
    //            new GenericContainer<>("bitnami/redis-sentinel:7.2.4")
    //
    //                    .withNetwork(network)
    //                    .withEnv("REDIS_SENTINEL_DOWN_AFTER_MILLISECONDS", "5000")
    //                    .withEnv("REDIS_SENTINEL_FAILOVER_TIMEOUT", "10000")
    //                    .withEnv("REDIS_SENTINEL_PASSWORD", password)
    //                    .withEnv("REDIS_MASTER_PASSWORD", password)
    //                    .withNetworkAliases("sentinel1")
    //                    .withExposedPorts(26379);
    //    sentinel1.start();
    //    int sentinel1Port = sentinel1.getFirstMappedPort();
    //    sentinel1.withCreateContainerCmdModifier(cmd -> {
    //        cmd.getHostConfig().withPortBindings(
    //                new PortBinding(Ports.Binding.bindPort(Integer.valueOf(sentinel1Port)),
    //                        cmd.getExposedPorts()[0]));
    //    });
    //    nodes.add(sentinel1);
    //
    //    GenericContainer<?> sentinel2 =
    //            new GenericContainer<>("bitnami/redis-sentinel:7.2.4")
    //                    .withNetwork(network)
    //                    .withEnv("REDIS_SENTINEL_DOWN_AFTER_MILLISECONDS", "5000")
    //                    .withEnv("REDIS_SENTINEL_FAILOVER_TIMEOUT", "10000")
    //                    .withEnv("REDIS_SENTINEL_PASSWORD", password)
    //                    .withEnv("REDIS_MASTER_PASSWORD", password)
    //                    .withNetworkAliases("sentinel2")
    //                    .withExposedPorts(26379);
    //    sentinel2.start();
    //    int sentinel2Port = sentinel2.getFirstMappedPort();
    //    sentinel2.withCreateContainerCmdModifier(cmd -> {
    //        cmd.getHostConfig().withPortBindings(
    //                new PortBinding(Ports.Binding.bindPort(Integer.valueOf(sentinel2Port)),
    //                        cmd.getExposedPorts()[0]));
    //    });
    //    nodes.add(sentinel2);
    //
    //    GenericContainer<?> sentinel3 =
    //            new GenericContainer<>("bitnami/redis-sentinel:7.2.4")
    //                    .withNetwork(network)
    //                    .withEnv("REDIS_SENTINEL_DOWN_AFTER_MILLISECONDS", "5000")
    //                    .withEnv("REDIS_SENTINEL_FAILOVER_TIMEOUT", "10000")
    //                    .withEnv("REDIS_SENTINEL_PASSWORD", password)
    //                    .withEnv("REDIS_MASTER_PASSWORD", password)
    //                    .withNetworkAliases("sentinel3")
    //                    .withExposedPorts(26379);
    //    sentinel3.start();
    //    int sentinel3Port = sentinel3.getFirstMappedPort();
    //    sentinel3.withCreateContainerCmdModifier(cmd -> {
    //        cmd.getHostConfig().withPortBindings(
    //                new PortBinding(Ports.Binding.bindPort(Integer.valueOf(sentinel3Port)),
    //                        cmd.getExposedPorts()[0]));
    //    });
    //    nodes.add(sentinel3);
    //
    //    Thread.sleep(5000);
    //
    //    Config config = new Config();
    //    config.setProtocol(protocol);
    //    config.useSentinelServers()
    //            .setPassword(password)
    //            .setNatMapper(new NatMapper() {
    //
    //                @Override
    //                public RedisURI map(RedisURI uri) {
    //                    for (GenericContainer<? extends GenericContainer<?>> node : nodes) {
    //                        if (node.getContainerInfo() == null) {
    //                            continue;
    //                        }
    //
    //                        Ports.Binding[] mappedPort = node.getContainerInfo().getNetworkSettings()
    //                                .getPorts().getBindings().get(new ExposedPort(uri.getPort()));
    //
    //                        Map<String, ContainerNetwork> ss = node.getContainerInfo().getNetworkSettings().getNetworks();
    //                        ContainerNetwork s = ss.values().iterator().next();
    //
    //                        if (uri.getPort() == 6379
    //                                && !uri.getHost().equals("redis")
    //                                && RedisDockerTest.this.getClass() == RedissonTopicTest.class
    //                                && node.getNetworkAliases().contains("slave0")) {
    //                            return new RedisURI(uri.getScheme(), "127.0.0.1", Integer.valueOf(mappedPort[0].getHostPortSpec()));
    //                        }
    //
    //                        if (mappedPort != null
    //                                && s.getIpAddress().equals(uri.getHost())) {
    //                            return new RedisURI(uri.getScheme(), "127.0.0.1", Integer.valueOf(mappedPort[0].getHostPortSpec()));
    //                        }
    //                    }
    //                    return uri;
    //                }
    //            })
    //            .addSentinelAddress("redis://127.0.0.1:" + sentinel1.getFirstMappedPort())
    //            .setMasterName("mymaster");
    //
    //    callback.accept(nodes, config);
    //
    //    nodes.forEach(n -> n.stop());
    //    network.close();
    //}

    /**
     * 使用通过 docker compose 文件创建的集群测试
     */
    //protected void withNewCluster(BiConsumer<List<ContainerState>, RedissonClient> callback) {
    //
    //    LogMessageWaitStrategy wait2 = new LogMessageWaitStrategy().withRegEx(".*REPLICA\ssync\\:\sFinished\swith\ssuccess.*");
    //
    //    DockerComposeContainer environment =
    //            new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
    //                    .withExposedService("redis-node-0", 6379)
    //                    .withExposedService("redis-node-1", 6379)
    //                    .withExposedService("redis-node-2", 6379)
    //                    .withExposedService("redis-node-3", 6379)
    //                    .withExposedService("redis-node-4", 6379)
    //                    .withExposedService("redis-node-5", 6379, wait2);
    //
    //    environment.start();
    //
    //    try {
    //        Thread.sleep(5000);
    //    } catch (InterruptedException e) {
    //        throw new RuntimeException(e);
    //    }
    //
    //    List<ContainerState> nodes = new ArrayList<>();
    //    for (int i = 0; i < 6; i++) {
    //        Optional<ContainerState> cc = environment.getContainerByServiceName("redis-node-" + i);
    //        nodes.add(cc.get());
    //    }
    //
    //    Optional<ContainerState> cc2 = environment.getContainerByServiceName("redis-node-0");
    //    Ports.Binding[] mp = cc2.get().getContainerInfo().getNetworkSettings()
    //            .getPorts().getBindings().get(new ExposedPort(cc2.get().getExposedPorts().get(0)));
    //
    //    Config config = new Config();
    //    config.useClusterServers()
    //            .setNatMapper(new NatMapper() {
    //
    //                @Override
    //                public RedisURI map(RedisURI uri) {
    //                    for (ContainerState state : nodes) {
    //                        if (state.getContainerInfo() == null) {
    //                            continue;
    //                        }
    //
    //                        InspectContainerResponse node = state.getContainerInfo();
    //                        Ports.Binding[] mappedPort = node.getNetworkSettings()
    //                                .getPorts().getBindings().get(new ExposedPort(uri.getPort()));
    //
    //                        Map<String, ContainerNetwork> ss = node.getNetworkSettings().getNetworks();
    //                        ContainerNetwork s = ss.values().iterator().next();
    //
    //                        if (mappedPort != null
    //                                && s.getIpAddress().equals(uri.getHost())) {
    //                            return new RedisURI(uri.getScheme(), "127.0.0.1", Integer.valueOf(mappedPort[0].getHostPortSpec()));
    //                        }
    //                    }
    //                    return uri;
    //                }
    //            })
    //            .addNodeAddress("redis://127.0.0.1:" + mp[0].getHostPortSpec());
    //
    //    RedissonClient redisson = Redisson.create(config);
    //
    //    try {
    //        callback.accept(nodes, redisson);
    //    } finally {
    //        redisson.shutdown();
    //        environment.stop();
    //    }
    //}

    //protected String execute(ContainerState node, String... commands) {
    //    try {
    //        Container.ExecResult r = node.execInContainer(commands);
    //        if (!r.getStderr().isBlank()) {
    //            throw new RuntimeException(r.getStderr());
    //        }
    //        return r.getStdout();
    //    } catch (IOException e) {
    //        throw new RuntimeException(e);
    //    } catch (InterruptedException e) {
    //        throw new RuntimeException(e);
    //    }
    //}

    //protected List<ContainerState> getSlaveNodes(List<ContainerState> nodes) {
    //    return nodes.stream().filter(node -> {
    //        if (!node.isRunning()) {
    //            return false;
    //        }
    //        String r = execute(node, "redis-cli", "info", "replication");
    //        if (r.contains("role:slave")) {
    //            return true;
    //        }
    //        return false;
    //    }).collect(Collectors.toList());
    //}

    //protected List<ContainerState> getMasterNodes(List<ContainerState> nodes) {
    //    return nodes.stream().filter(node -> {
    //        if (!node.isRunning()) {
    //            return false;
    //        }
    //        String r = execute(node, "redis-cli", "info", "replication");
    //        if (r.contains("role:master")) {
    //            return true;
    //        }
    //        return false;
    //    }).collect(Collectors.toList());
    //}

    //protected void stop(ContainerState node) {
    //    execute(node, "redis-cli", "shutdown");
    //}

    //protected void restart(GenericContainer<?> redis) {
    //    redis.setPortBindings(Arrays.asList(redis.getFirstMappedPort() + ":" + redis.getExposedPorts().get(0)));
    //    redis.stop();
    //    redis.start();
    //}

}
