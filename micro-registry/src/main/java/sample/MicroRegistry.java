package sample;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Eureka Server の起動クラス。
 * <p>本クラスを実行する事でSpringBootが提供する組込Tomcatでのアプリケーション起動が行われます。
 */
@SpringBootApplication
@EnableEurekaServer
public class MicroRegistry {
    
    public static void main(String[] args) {
        new SpringApplicationBuilder(MicroRegistry.class)
            .run(args);
    }
    
    /** プロセススコープの拡張定義を表現します。 */
    @Configuration
    static class ProcessAutoConfig {
        
        /** テスト用途のメモリDBサーバ  */
        @Bean(initMethod="start", destroyMethod = "stop")
        @ConditionalOnProperty(prefix = "extension.test.db", name = "enabled", matchIfMissing = false)
        Server h2Server() {
            try {
                return Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "9092");
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }
    }
    
    @Configuration
    static class AuthSecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // レジストリサーバでは認証を常に許可
            http.csrf().disable()            
                .authorizeRequests()
                .mvcMatchers("/**").permitAll();
        }

    }

}
