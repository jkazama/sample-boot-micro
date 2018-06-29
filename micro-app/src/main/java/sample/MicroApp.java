package sample;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import sample.api.MasterFacadeExporter;
import sample.usecase.AccountService;

/**
 * アプリケーションプロセスの起動クラス。
 * <p>本クラスを実行する事でSpringBootが提供する組込Tomcatでのアプリケーション起動が行われます。
 * <p>自動的に Eureka Server へ登録されます。
 * <p>自動設定対象として以下のパッケージをスキャンしています。
 * <ul>
 * <li>sample.usecase
 * <li>sample.api
 * </ul>
 */
@SpringBootApplication(scanBasePackageClasses = {
    AccountService.class, MasterFacadeExporter.class })
@Import(MicroAppConfig.class)
@EnableCaching(proxyTargetClass = true)
@EnableDiscoveryClient
public class MicroApp {
    
    public static void main(String[] args) {
        new SpringApplicationBuilder(MicroApp.class)
            .profiles("app")
            .run(args);
    }
    
}
