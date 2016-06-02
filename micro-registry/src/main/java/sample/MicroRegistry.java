package sample;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.*;

/**
 * アプリケーションプロセスの起動クラス。
 * <p>本クラスを実行する事でSpringBootが提供する組込Tomcatでのアプリケーション起動が行われます。
 */
@SpringBootApplication
@EnableEurekaServer
@EnableDiscoveryClient
public class MicroRegistry {
    public static void main(String[] args) {
        new SpringApplicationBuilder(MicroRegistry.class)
            .run(args);
    }
       
    @Configuration
    static class MicroRegistryConfig {
        /** テスト用途のメモリDBサーバ  */
        @Bean(initMethod="start", destroyMethod = "stop")
        Server h2Server() {
            try {
                return Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "9092");
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }
    }

}
