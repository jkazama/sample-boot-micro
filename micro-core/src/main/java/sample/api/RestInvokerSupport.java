package sample.api;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import sample.context.rest.RestInvoker;

/**
 * Spring Cloud Netflix 標準の API クライアント要求アプローチをサポートします。
 * <p>API クライアント側の FacadeInvoker 基底処理として利用してください。
 */
public abstract class RestInvokerSupport {
    
    /** 接続対象となるアプリケーション名称 */
    public abstract String applicationName();
    /** 対象接続先のルートパス ( 「/」を先頭としたルートパス指定 ) */
    public String rootPath() {
        return null;
    }
    
    @Autowired
    @LoadBalanced
    protected RestTemplate template;
    @Autowired
    protected ObjectMapper mapper;
    
    private Optional<RestInvoker> invoker = Optional.empty();

    /** 初期化処理を行います。 */
    @PostConstruct
    public void initialize() {
        invoker = Optional.of(new RestInvoker(template, mapper, rootUrl()));
    }
    
    /** API 接続先ルートとなる URL を返します。 */
    protected String rootUrl() {
        return "http://" + applicationName() + Optional.ofNullable(rootPath()).orElse("");
    }

    /** Ribbon を用いた RestInvoker を返します。 */
    protected RestInvoker invoker() {
        return invoker.orElseThrow(() -> new IllegalStateException("事前に initialize メソッドを呼び出してください"));
    }
        
}
