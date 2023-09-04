package cn.iocoder.springboot.lab27.springwebflux;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class HttpReactiveHandlerTest {

    /**
     * 剥离业务后看WebFlux Http请求的响应式处理
     */
    @Test
    public void testHandle() throws InterruptedException {
        //对应WebFlux中是HandlerMapping的链表
        List<Integer> mappings = new ArrayList<>(); //假设这个是HandlerMapping的链表
        mappings.add(1);
        Mono<Void> mono = Flux.fromIterable(mappings)
                //将此Flux发出的元素异步转换为publisher，然后将这些内部publisher平铺成单个Flux，并使用串联保持顺序
                //对应WebFlux中是异步查询和当前请求匹配的Handler
                .concatMap(mapping -> {
                    System.out.println("get matched handler");
                    String handler = String.valueOf(mapping);
                    return Mono.justOrEmpty(handler);
                })
                //只发送Flux中第一个元素到新的Mono中，如果Flux为空就创建空的Mono
                //对应WebFlux中只返回第一个匹配的Handler
                .next()
                //如果为空的处理
                //对应WebFlux中如果没有匹配的Handler就返回抛异常的Mono
                .switchIfEmpty(Mono.defer(() -> {
                    Exception ex = new RuntimeException("No matching handler");
                    return Mono.error(ex);
                }))
                //对应WebFlux中如果能走到这里说明至少有一个Handler,调用Handler的处理逻辑
                .flatMap(handler -> {
                    Function<Long, String> handlerMethod = reqData -> reqData + "D";
                    return Mono.empty()    //对应WebFlux这一步处理session、attr等的初始化
                            .then(Mono.defer(() -> {
                                //WebFlux中请求数据是ServerWebExchange带进来的
                                long reqData = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                                System.out.println("invoke handler method");        //这里面还有很多逻辑
                                String result = handlerMethod.apply(reqData);
                                return Mono.just(result);
                            }))
                            .doOnNext(result -> System.out.println("set ExceptionHandler to result"))
                            .doOnNext(result -> System.out.println("save model in bindContext"))
                            //如果发生异常用Mono.error取代
                            .onErrorResume(ex -> {
                                System.out.println(ex.getMessage());
                                return Mono.error(ex);
                            });
                })
                //对应WebFlux中走到这里请求已经处理，这步用于处理响应结果
                .flatMap(result -> {
                    //WebFlux中这一步将响应写入response对象
                    System.out.println("handle result: " + result);             //这里面还有很多逻辑
                    return Mono.empty();
                });

        Mono<Void> defer = Mono.defer(() -> mono)
                .onErrorResume(ex -> {
                    System.out.println("handler (ExceptionHandlingWebHandler) handle exception and set response status");
                    return Mono.error(ex);
                })
                .doOnSuccess(aVoid -> System.out.println("request success"))
                .onErrorResume(ex -> {
                    System.out.println("handler (ReactorHttpHandlerAdapter) handle exception");
                    //ReactorHttpHandlerAdapter
                    return Mono.error(ex);
                })
                .then();
        //.doOnError()
        //.doOnSuccess()

        Mono.fromDirect(defer)
                .subscribe();

        Thread.sleep(1000);
    }
}
