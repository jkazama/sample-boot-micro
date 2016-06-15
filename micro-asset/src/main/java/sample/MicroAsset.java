package sample;

import org.springframework.boot.actuate.health.*;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.*;

import sample.api.RestInvokerSupport;
import sample.context.Timestamper;
import sample.context.actor.ActorSession;
import sample.context.orm.DefaultRepository;
import sample.context.rest.RestActorSessionBindFilter;
import sample.microasset.api.AssetFacadeExporter;
import sample.microasset.context.orm.*;
import sample.microasset.model.AssetDataFixtures;
import sample.microasset.usecase.AssetService;
import sample.model.*;
import sample.model.BusinessDayHandler.HolidayAccessor;
import sample.usecase.ServiceSupport;

/**
 * 資産アプリケーションプロセスの起動クラス。
 * <p>本クラスを実行する事でSpringBootが提供する組込Tomcatでのアプリケーション起動が行われます。
 * <p>自動的に Eureka Server へ登録されます。
 * <p>自動設定対象として以下のパッケージをスキャンしています。
 * <ul>
 * <li>sample.context
 * <li>sample.usecase
 * <li>sample.api
 * <li>sample.microasset.usecase
 * <li>sample.microasset.api
 * </ul>
 */
@SpringBootApplication(scanBasePackageClasses = {
    Timestamper.class, ServiceSupport.class, RestInvokerSupport.class,
    AssetService.class, AssetFacadeExporter.class })
@Import({ ApplicationConfig.class, AssetRepository.class })
@EnableCaching(proxyTargetClass = true)
@EnableDiscoveryClient
public class MicroAsset {
    
    public static void main(String[] args) {
        new SpringApplicationBuilder(MicroAsset.class)
            .profiles("asset")
            .run(args);
    }
    
    /** Domain 層のコンテナ管理を表現します。 */
    @Configuration
    static class DomainAutoConfig {
        
        /** データ生成ユーティリティ */
        @Bean
        @ConditionalOnProperty(prefix = DataFixtures.Prefix, name = "enabled", matchIfMissing = false)
        AssetDataFixtures fixtures() {
            return new AssetDataFixtures();
        }
        
        /** 休日情報アクセサ */
        @Bean
        HolidayAccessor holidayAccessor(DefaultRepository rep) {
            return new HolidayAccessor(rep);
        }
        
        /** 営業日ユーティリティ */
        @Bean
        BusinessDayHandler businessDayHandler(Timestamper time, HolidayAccessor holidayAccessor) {
            return new BusinessDayHandler(time, holidayAccessor);
        }
    }
    
    /** プロセススコープの拡張定義を表現します。 */
    @Configuration
    static class ProcessAutoConfig {
        
        /**
         * リクエストに利用者情報が設定されていた時はそのままスレッドローカルへ紐づけます。
         * <p>同期Servletでのみ適切に動きます。
         */
        @Bean
        public RestActorSessionBindFilter restActorSessionBindFilter(ActorSession session) {
            return new RestActorSessionBindFilter(session);
        }
        
    }
    
    /** 拡張ヘルスチェック定義を表現します。 */
    @Configuration
    static class HealthCheckAuthConfig {
        
        /** 営業日チェック */
        @Bean
        HealthIndicator dayIndicator(final Timestamper time, final BusinessDayHandler day) {
            return new AbstractHealthIndicator() {
                @Override
                protected void doHealthCheck(Builder builder) throws Exception {
                    builder.up();
                    builder.withDetail("day", day.day())
                            .withDetail("dayMinus1", day.day(-1))
                            .withDetail("dayPlus1", day.day(1))
                            .withDetail("dayPlus2", day.day(2))
                            .withDetail("dayPlus3", day.day(3));
                }
            };
        }
    }

}
