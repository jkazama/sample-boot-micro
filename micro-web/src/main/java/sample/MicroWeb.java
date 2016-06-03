package sample;

import java.util.*;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

import sample.api.AssetFacadeInvoker;
import sample.context.Timestamper;
import sample.context.actor.ActorSession;
import sample.context.rest.RestActorSessionInterceptor;
import sample.controller.AccountController;
import sample.usecase.SecurityService;

/**
 * Webフロントプロセスの起動クラス。
 * <p>本クラスを実行する事でSpringBootが提供する組込Tomcatでのアプリケーション起動が行われます。
 * <p>自動設定対象として以下のパッケージをスキャンしています。
 * <ul>
 * <li>sample.context
 * <li>sample.api
 * <li>sample.controller
 * </ul>
 */
@SpringBootApplication(scanBasePackageClasses = {
    Timestamper.class, AssetFacadeInvoker.class, AccountController.class })
@Import({ ApplicationConfig.class, SecurityService.class })
@EnableCaching(proxyTargetClass = true)
@EnableDiscoveryClient
@EnableFeignClients(basePackageClasses = AssetFacadeInvoker.class )
public class MicroWeb {
    
    public static void main(String[] args) {
        new SpringApplicationBuilder(MicroWeb.class)
            .profiles("web")
            .run(args);
    }
    
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
