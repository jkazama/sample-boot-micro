package sample;

import java.util.*;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

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
