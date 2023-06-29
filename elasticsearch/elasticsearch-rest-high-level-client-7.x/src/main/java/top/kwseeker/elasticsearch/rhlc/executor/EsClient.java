package top.kwseeker.elasticsearch.rhlc.executor;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import top.kwseeker.elasticsearch.rhlc.common.exception.EsErrorCode;
import top.kwseeker.elasticsearch.rhlc.common.exception.EsException;

import java.io.IOException;

@Slf4j
public class EsClient {

    private static final String SCHEMA = "http";

    @SuppressWarnings("deprecation")
    private RestHighLevelClient restHighLevelClient;
    private final String nodes;

    public EsClient(String nodes) {
        this.nodes = nodes;
        this.restHighLevelClient = buildClient();
    }

    @SuppressWarnings("deprecation")
    private RestHighLevelClient buildClient() {
        try {
            String[] hosts = nodes.trim().split(",");
            //将字符串数组hosts转成org.apache.http.HttpHost数组
            HttpHost[] httpHosts = new HttpHost[hosts.length];
            for (int i = 0; i < hosts.length; i++) {
                String[] splitHost = hosts[i].split(":");
                httpHosts[i] = new HttpHost(splitHost[0], Integer.parseInt(splitHost[1]), SCHEMA);
            }

            return new RestHighLevelClient(RestClient.builder(httpHosts));
        } catch (Exception e) {
            throw new EsException(EsErrorCode.FAILURE, e);
        }
    }

    public void close() {
        try {
            if (restHighLevelClient != null) {
                restHighLevelClient.close();
                restHighLevelClient = null;
            }
        } catch (IOException e) {
            throw new EsException(EsErrorCode.FAILURE, e);
        }
    }

    @SuppressWarnings("deprecation")
    public RestHighLevelClient getRestHighLevelClient() {
        if (restHighLevelClient == null) {
            restHighLevelClient = buildClient();
        }
        return restHighLevelClient;
    }

    public String getNodes() {
        return nodes;
    }
}
