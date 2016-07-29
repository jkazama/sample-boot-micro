package sample.microasset;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import sample.microasset.api.AssetFacadeExporter;
import sample.microasset.usecase.AssetService;

/**
 * 資産アプリケーションプロセスの起動クラス。
 * <p>本クラスを実行する事でSpringBootが提供する組込Tomcatでのアプリケーション起動が行われます。
 * <p>自動的に Eureka Server へ登録されます。
 * <p>自動設定対象として以下のパッケージをスキャンしています。
 * <ul>
 * <li>sample.microasset.usecase
 * <li>sample.microasset.api
 * </ul>
 */
@SpringBootApplication(scanBasePackageClasses = { AssetService.class, AssetFacadeExporter.class })
@Import(MicroAssetConfig.class)
@EnableCaching(proxyTargetClass = true)
@EnableDiscoveryClient
public class MicroAsset {
    
    public static void main(String[] args) {
        new SpringApplicationBuilder(MicroAsset.class)
            .profiles("asset")
            .run(args);
    }

}
