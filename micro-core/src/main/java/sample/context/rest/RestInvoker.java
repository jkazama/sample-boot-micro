package sample.context.rest;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import sample.context.Dto;

/**
 * RestTemplate に対する簡易アクセサを提供します。
 */
public class RestInvoker {

    /** 内部で利用する RestTemplate */
    private final RestTemplate template;
    /** クエリパラメタ構築用オブジェクトコンバータ */
    private final ObjectMapper mapper;
    /** 実行時のルートURL */
    private final String rootUrl;
    
    public RestInvoker(RestTemplate template, ObjectMapper mapper, String rootUrl) {
        this.template = template;
        this.mapper = mapper;
        this.rootUrl = rootUrl;
    }
    
    /**
     * API に対して GET 要求します。
     */
    public <T> T get(String path, Class<T> responseType, Object... variables) {
        return template.getForObject(servicePath(path), responseType, variables);
    }
    
    /**
     * 指定したパスに接続先サービスルートURLを付与して返します。
     * <P>パスは 「/」 を先頭にして記述してください。 
     */
    public String servicePath(String path) {
        return Optional.ofNullable(this.rootUrl).orElse("") + path;
    }
    
    /**
     * API に対して GET 要求します。
     * <p>戻り値に総称型を必要とする時はこちらを利用してください
     */
    public <T> T get(String path, ParameterizedTypeReference<T> responseType, Object... variables) {
        return template.exchange(servicePath(path), HttpMethod.GET, HttpEntity.EMPTY, responseType, variables).getBody();
    }
    
    /**
     * API に対して GET 要求します。 
     * <p>request に設定された bean はプロパティ-値のペアで path に結合されます
     */
    public <T> T get(String path, Class<T> responseType, Dto request, Object... variables) {
        return template.getForObject(servicePath(path, request), responseType, variables);
    }
    
    /**
     * 指定したパスに接続先サービスルートURLとリクエストパラメタを末尾付与して返します。
     * <P>パスは 「/」 を先頭にして記述してください。
     * <p>リクエストパラメタは簡易的にネストオブジェクトをサポートしますが、あまり階層深く作らないように注意してください。
     */
    @SuppressWarnings("unchecked")
    public String servicePath(String path, Dto request) {
        if (request == null) {
            return servicePath(path);
        } else {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(servicePath(path));
            mapper.convertValue(request, Map.class).forEach((k, v) ->
                buildQuery(builder, k.toString(), v));
            return builder.build().toUriString();
        }
    }
    
    protected void buildQuery(UriComponentsBuilder builder, String key, Object value) {
        if (value == null) return;
        if (value.getClass().isArray()) {
            if (((Object[])value).length == 0) return;
            builder.queryParam(key, (Object[])value);
        } else if (value instanceof List) {
            if (((List<?>)value).isEmpty()) return;
            builder.queryParam(key, ((List<?>)value).toArray());
        } else if (value instanceof Map) {
            ((Map<?, ?>)value).forEach((k, v) ->
                buildQuery(builder, key + "." + k, v));
        } else {
            builder.queryParam(key, value);
        }
    }
    
    /**
     * API に対して GET 要求します。
     * <p>request に設定された bean はプロパティ-値のペアで path に結合されます
     * <p>戻り値に総称型を必要とする時はこちらを利用してください
     */
    public <T> T get(String path, ParameterizedTypeReference<T> responseType, Dto request, Object... variables) {
        return template.exchange(servicePath(path, request), HttpMethod.GET, HttpEntity.EMPTY, responseType, variables).getBody();
    }
    
    /**
     * API に対して GET 要求します。
     */
    public <T> ResponseEntity<T> getEntity(String path, Class<T> responseType, Object... variables) {
        return template.getForEntity(servicePath(path), responseType, variables);
    }
    
    /**
     * API に対して GET 要求します。
     * <p>戻り値に総称型を必要とする時はこちらを利用してください
     */
    public <T> ResponseEntity<T> getEntity(String path, ParameterizedTypeReference<T> responseType, Object... variables) {
        return template.exchange(servicePath(path), HttpMethod.GET, HttpEntity.EMPTY, responseType, variables);
    }
            
    /**
     * API に対して GET 要求します。 
     * <p>request に設定された bean はプロパティ-値のペアで path に結合されます
     */
    public <T> ResponseEntity<T> getEntity(String path, Class<T> responseType, Dto request, Object... variables) {
        return template.getForEntity(servicePath(path, request), responseType, variables);
    }

    /**
     * API に対して GET 要求します。 
     * <p>request に設定された bean はプロパティ-値のペアで path に結合されます
     * <p>戻り値に総称型を必要とする時はこちらを利用してください
     */
    public <T> ResponseEntity<T> getEntity(String path, ParameterizedTypeReference<T> responseType, Dto request, Object... variables) {
        return template.exchange(servicePath(path, request), HttpMethod.GET, HttpEntity.EMPTY, responseType, variables);
    }
    
    /**
     * API に対して POST 要求します。
     * <p>request 値は application/json で要求されます。受信側は忘れずにバインド変数へ &#64;RequestBody を付与してください。
     */
    public <T> T post(String path, Class<T> responseType, Dto request, Object... variables) {
        return template.postForObject(servicePath(path), request, responseType, variables);
    }
    
    /**
     * API に対して POST 要求します。
     * <p>request 値は application/json で要求されます。受信側は忘れずにバインド変数へ &#64;RequestBody を付与してください。
     * <p>戻り値に総称型を必要とする時はこちらを利用してください
     */
    public <T> T post(String path, ParameterizedTypeReference<T> responseType, Dto request, Object... variables) {
        return template.exchange(servicePath(path), HttpMethod.POST, new HttpEntity<>(request), responseType, variables).getBody();
    }
    
    /**
     * API に対して POST 要求します。
     * <p>request 値は application/json で要求されます。受信側は忘れずにバインド変数へ &#64;RequestBody を付与してください。
     */
    public <T> ResponseEntity<T> postEntity(String path, Class<T> responseType, Dto request, Object... variables) {
        return template.postForEntity(servicePath(path), request, responseType, variables);
    }
    
    /**
     * API に対して POST 要求します。
     * <p>request 値は application/json で要求されます。受信側は忘れずにバインド変数へ &#64;RequestBody を付与してください。
     * <p>戻り値に総称型を必要とする時はこちらを利用してください
     */
    public <T> ResponseEntity<T> postEntity(String path, ParameterizedTypeReference<T> responseType, Dto request, Object... variables) {
        return template.exchange(servicePath(path), HttpMethod.POST, new HttpEntity<>(request), responseType, variables);
    }

    /**
     * API に対して POST 要求します。
     * <p>request 値は application/x-www-form-urlencoded で要求されます
     */
    public <T> T postForm(String path, Class<T> responseType, Dto request, Object... variables) {
        return template.exchange(servicePath(path), HttpMethod.POST,
                formEntity(request), responseType, variables).getBody();
    }
    
    @SuppressWarnings("unchecked")
    protected HttpEntity<MultiValueMap<String, Object>> formEntity(Dto request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        mapper.convertValue(request, Map.class).forEach((k, v) ->
            buildMap(map, k.toString(), v));
        return new HttpEntity<>(map, headers);
    }
    
    @SuppressWarnings("unchecked")
    protected void buildMap(MultiValueMap<String, Object> map, String key, Object value) {
        if (value == null) return;
        if (value.getClass().isArray()) {
            if (((Object[])value).length == 0) return;
            map.put(key, Arrays.asList((Object[])value).stream()
                            .map(Object::toString)
                            .collect(Collectors.toList()));
        } else if (value instanceof List) {
            if (((List<Object>)value).isEmpty()) return;
            map.put(key, (List<Object>)value).stream()
                            .map(Object::toString)
                            .collect(Collectors.toList());
        } else if (value instanceof Map) {
            ((Map<?, ?>)value).forEach((k, v) ->
                buildMap(map, key + "." + k, v));
        } else {
            map.add(key, value.toString());
        }
    }
    
    /**
     * API に対して POST 要求します。
     * <p>request 値は application/x-www-form-urlencoded で要求されます
     * <p>戻り値に総称型を必要とする時はこちらを利用してください
     */
    public <T> T postForm(String path, ParameterizedTypeReference<T> responseType, Dto request, Object... variables) {
        return template.exchange(servicePath(path), HttpMethod.POST,
                formEntity(request), responseType, variables).getBody();
    }
    
    /**
     * API に対して POST 要求します。
     * <p>request 値は application/x-www-form-urlencoded で要求されます
     */
    public <T> ResponseEntity<T> postFormEntity(String path, Class<T> responseType, Dto request, Object... variables) {
        return template.exchange(servicePath(path), HttpMethod.POST,
                formEntity(request), responseType, variables);
    }

    /**
     * API に対して POST 要求します。
     * <p>request 値は application/x-www-form-urlencoded で要求されます
     * <p>戻り値に総称型を必要とする時はこちらを利用してください
     */
    public <T> ResponseEntity<T> postFormEntity(String path, ParameterizedTypeReference<T> responseType, Dto request, Object... variables) {
        return template.exchange(servicePath(path), HttpMethod.POST,
                formEntity(request), responseType, variables);
    }
    
}
