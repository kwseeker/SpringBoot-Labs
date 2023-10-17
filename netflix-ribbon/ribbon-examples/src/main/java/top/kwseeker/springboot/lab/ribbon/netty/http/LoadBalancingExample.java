package top.kwseeker.springboot.lab.ribbon.netty.http;

import com.google.common.collect.Lists;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.LoadBalancerBuilder;
import com.netflix.loadbalancer.Server;
import com.netflix.ribbon.transport.netty.RibbonTransport;
import com.netflix.ribbon.transport.netty.http.LoadBalancingHttpClient;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import rx.Observer;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class LoadBalancingExample {

    public static void main(String[] args) throws InterruptedException {
        ArrayList<Server> servers = Lists.newArrayList(
                new Server("www.baidu.com", 80),
                new Server("www.linkedin.com", 80),
                new Server("www.yahoo.com", 80));

        //创建客户端 ribbon-transport
        BaseLoadBalancer lb = LoadBalancerBuilder.newBuilder().buildFixedServerListLoadBalancer(servers);
        LoadBalancingHttpClient<ByteBuf, ByteBuf> httpClient = RibbonTransport.newHttpClient(lb);

        final CountDownLatch latch = new CountDownLatch(servers.size());

        HttpClientResponseObserver observer = new HttpClientResponseObserver(latch);

        for (int i = 0; i < servers.size(); i++) {
            HttpClientRequest<ByteBuf> request = HttpClientRequest.createGet("/");
            httpClient.submit(request).subscribe(observer);
        }
        latch.await();
        System.out.println(lb.getLoadBalancerStats());
    }

    static class HttpClientResponseObserver implements Observer<HttpClientResponse<ByteBuf>> {

        private final CountDownLatch latch;

        public HttpClientResponseObserver(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(HttpClientResponse<ByteBuf> args) {
            latch.countDown();
            System.out.println("Got response: " + args.getStatus());
        }
    }
}
