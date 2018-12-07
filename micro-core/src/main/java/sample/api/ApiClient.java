package sample.api;

import java.util.Optional;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import sample.context.rest.RestInvoker;

/**
 * Spring Cloud Netflix 標準の API クライアント要求アプローチをサポートします。
 * <p>API クライアント側の Facade で本コンポーネントから RestInvoker を取得して実行してください。
 */
public class ApiClient {

    private final RestTemplate template;
    private final ObjectMapper mapper;

    public ApiClient(RestTemplate template, ObjectMapper mapper) {
        this.template = template;
        this.mapper = mapper;
    }

    /** Ribbon を用いた RestInvoker を返します。 */
    public RestInvoker invoker(String applicationName, String rootPath) {
        return new RestInvoker(this.template, this.mapper, rootUrl(applicationName, rootPath));
    }

    /** API 接続先ルートとなる URL を返します。 */
    private String rootUrl(String applicationName, String rootPath) {
        return "http://" + applicationName + Optional.ofNullable(rootPath).orElse("");
    }

    public static ApiClient of(RestTemplate template, ObjectMapper mapper) {
        return new ApiClient(template, mapper);
    }

}
