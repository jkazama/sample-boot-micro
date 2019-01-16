package sample;

import java.util.*;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import sample.api.ApiClient;
import sample.context.actor.ActorSession;
import sample.context.rest.RestActorSessionInterceptor;
import sample.usecase.SecurityService;

/**
 * アプリケーションのセキュリティ定義を表現します。
 */
@Configuration
@Import({ApplicationConfig.class, SecurityService.class})
public class MicroWebConfig {
    
    /** Spring Cloud 関連の定義を表現します。 */
    @Configuration
    static class DiscoveryAutoConfig {
        
        @Bean
        ApiClient apiClient(RestTemplate template, ObjectMapper mapper) {
            return ApiClient.of(template, mapper);
        }
        
        /**
         * Ribbon 経由で Eureka がサポートしているサービスを実行するための RestTemplate。
         * <p>リクエスト時に利用者情報を紐づけています。
         */
        @Bean
        @LoadBalanced
        RestTemplate restTemplate(ActorSession session) {
            RestTemplate tmpl = new RestTemplate();
            tmpl.setInterceptors(new ArrayList<>(Arrays.asList(
                new RestActorSessionInterceptor(session)
            )));
            return tmpl;
        }
        
    }
    
}
