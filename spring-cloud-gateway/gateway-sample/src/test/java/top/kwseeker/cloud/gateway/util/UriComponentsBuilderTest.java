package top.kwseeker.cloud.gateway.util;

import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class UriComponentsBuilderTest {

    @Test
    public void test() {
        URI uri = URI.create("http://localhost:8080/echo/hello");
        URI routeUri = URI.create("lb://service-echo");
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri)
                .scheme(routeUri.getScheme())
                .host(routeUri.getHost())
                .port(routeUri.getPort());
        URI mergedUrl = builder.build(false).toUri();
        System.out.println(mergedUrl);
    }
}
