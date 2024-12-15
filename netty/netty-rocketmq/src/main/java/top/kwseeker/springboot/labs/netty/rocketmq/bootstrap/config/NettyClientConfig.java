package top.kwseeker.springboot.labs.netty.rocketmq.bootstrap.config;

public class NettyClientConfig {

    private int clientCallbackExecutorThreads = Runtime.getRuntime().availableProcessors();
    private int clientWorkerThreads = 4;
    private int connectTimeoutMillis = 3000;
    private boolean disableNettyWorkerGroup = true;
    private int clientChannelMaxIdleTimeSeconds = 120;
    private int clientAsyncSemaphoreValue = 65535;

    private int clientSocketSndBufSize = 0;
    private int clientSocketRcvBufSize = 0;
    private int writeBufferHighWaterMark = 0;
    private int writeBufferLowWaterMark = 0;
    private boolean clientPooledByteBufAllocatorEnable = false;
    private boolean disableCallbackExecutor = false;

    private boolean enableReconnectForGoAway = true;
    private boolean enableTransparentRetry = true;
    private long maxReconnectIntervalTimeSeconds = 60;

    public int getClientCallbackExecutorThreads() {
        return clientCallbackExecutorThreads;
    }

    public void setClientCallbackExecutorThreads(int clientCallbackExecutorThreads) {
        this.clientCallbackExecutorThreads = clientCallbackExecutorThreads;
    }

    public int getClientWorkerThreads() {
        return clientWorkerThreads;
    }

    public void setClientWorkerThreads(int clientWorkerThreads) {
        this.clientWorkerThreads = clientWorkerThreads;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public boolean isDisableNettyWorkerGroup() {
        return disableNettyWorkerGroup;
    }

    public void setDisableNettyWorkerGroup(boolean disableNettyWorkerGroup) {
        this.disableNettyWorkerGroup = disableNettyWorkerGroup;
    }

    public int getClientChannelMaxIdleTimeSeconds() {
        return clientChannelMaxIdleTimeSeconds;
    }

    public void setClientChannelMaxIdleTimeSeconds(int clientChannelMaxIdleTimeSeconds) {
        this.clientChannelMaxIdleTimeSeconds = clientChannelMaxIdleTimeSeconds;
    }

    public int getClientAsyncSemaphoreValue() {
        return clientAsyncSemaphoreValue;
    }

    public void setClientAsyncSemaphoreValue(int clientAsyncSemaphoreValue) {
        this.clientAsyncSemaphoreValue = clientAsyncSemaphoreValue;
    }

    public int getClientSocketSndBufSize() {
        return clientSocketSndBufSize;
    }

    public void setClientSocketSndBufSize(int clientSocketSndBufSize) {
        this.clientSocketSndBufSize = clientSocketSndBufSize;
    }

    public int getClientSocketRcvBufSize() {
        return clientSocketRcvBufSize;
    }

    public void setClientSocketRcvBufSize(int clientSocketRcvBufSize) {
        this.clientSocketRcvBufSize = clientSocketRcvBufSize;
    }

    public int getWriteBufferHighWaterMark() {
        return writeBufferHighWaterMark;
    }

    public void setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        this.writeBufferHighWaterMark = writeBufferHighWaterMark;
    }

    public int getWriteBufferLowWaterMark() {
        return writeBufferLowWaterMark;
    }

    public void setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        this.writeBufferLowWaterMark = writeBufferLowWaterMark;
    }

    public boolean isClientPooledByteBufAllocatorEnable() {
        return clientPooledByteBufAllocatorEnable;
    }

    public void setClientPooledByteBufAllocatorEnable(boolean clientPooledByteBufAllocatorEnable) {
        this.clientPooledByteBufAllocatorEnable = clientPooledByteBufAllocatorEnable;
    }

    public boolean isDisableCallbackExecutor() {
        return disableCallbackExecutor;
    }

    public void setDisableCallbackExecutor(boolean disableCallbackExecutor) {
        this.disableCallbackExecutor = disableCallbackExecutor;
    }

    public boolean isEnableReconnectForGoAway() {
        return enableReconnectForGoAway;
    }

    public void setEnableReconnectForGoAway(boolean enableReconnectForGoAway) {
        this.enableReconnectForGoAway = enableReconnectForGoAway;
    }

    public boolean isEnableTransparentRetry() {
        return enableTransparentRetry;
    }

    public void setEnableTransparentRetry(boolean enableTransparentRetry) {
        this.enableTransparentRetry = enableTransparentRetry;
    }

    public long getMaxReconnectIntervalTimeSeconds() {
        return maxReconnectIntervalTimeSeconds;
    }

    public void setMaxReconnectIntervalTimeSeconds(long maxReconnectIntervalTimeSeconds) {
        this.maxReconnectIntervalTimeSeconds = maxReconnectIntervalTimeSeconds;
    }
}