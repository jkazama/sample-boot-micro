package sample;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import sample.api.AssetFacadeInvoker;
import sample.controller.AccountController;

/**
 * Webフロントプロセスの起動クラス。
 * <p>本クラスを実行する事でSpringBootが提供する組込Tomcatでのアプリケーション起動が行われます。
 * <p>自動設定対象として以下のパッケージをスキャンしています。
 * <ul>
 * <li>sample.api
 * <li>sample.controller
 * </ul>
 */
@SpringBootApplication(scanBasePackageClasses = {
    AssetFacadeInvoker.class, AccountController.class })
@Import(MicroWebConfig.class)
@EnableCaching(proxyTargetClass = true)
@EnableDiscoveryClient
public class MicroWeb {
    
    public static void main(String[] args) {
        new SpringApplicationBuilder(MicroWeb.class)
            .profiles("web")
            .run(args);
    }
        
}
