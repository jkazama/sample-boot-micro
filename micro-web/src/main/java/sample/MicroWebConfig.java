package sample;

import java.util.*;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.client.RestTemplate;

import sample.context.actor.ActorSession;
import sample.context.rest.RestActorSessionInterceptor;
import sample.context.security.*;
import sample.context.security.SecurityConfigurer.*;
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
    
    /** Spring Security を用いた API 認証/認可定義を表現します。 */
    @Configuration
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
    @ConditionalOnProperty(prefix = "extension.security.auth", name = "enabled", matchIfMissing = true)
    @Order(org.springframework.boot.autoconfigure.security.SecurityProperties.BASIC_AUTH_ORDER)
    static class AuthSecurityConfig {
    
        /** Spring Security 全般の設定 ( 認証/認可 ) を定義します。 */
        @Bean
        @Order(org.springframework.boot.autoconfigure.security.SecurityProperties.BASIC_AUTH_ORDER)
        SecurityConfigurer securityConfigurer() {
            return new SecurityConfigurer();
        }
        
        /** Spring Security のカスタム認証プロセス管理コンポーネント。 */
        @Bean
        AuthenticationManager authenticationManager() throws Exception {
            return securityConfigurer().authenticationManagerBean();
        }
        
        /** Spring Security のカスタム認証プロバイダ。 */
        @Bean
        SecurityProvider securityProvider() {
            return new SecurityProvider();
        }
        
        /** Spring Security のカスタムエントリポイント。 */
        @Bean
        SecurityEntryPoint securityEntryPoint() {
            return new SecurityEntryPoint();
        }
        
        /** Spring Security におけるログイン/ログアウト時の振る舞いを拡張するHandler。 */
        @Bean
        LoginHandler loginHandler() {
            return new LoginHandler();
        }
        
        /** Spring Security で利用される認証/認可対象となるユーザ情報を提供します。 */
        @Bean
        SecurityActorFinder securityActorFinder() {
            return new SecurityActorFinder();
        }
    }    
}
