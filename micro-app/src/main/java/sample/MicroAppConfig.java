package sample;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.*;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.transaction.PlatformTransactionManager;

import sample.context.Timestamper;
import sample.context.actor.ActorSession;
import sample.context.orm.DefaultRepository;
import sample.context.rest.RestActorSessionBindFilter;
import sample.model.*;
import sample.model.BusinessDayHandler.HolidayAccessor;

/**
 * アプリケーションドメインのコンポーネント定義を表現します。
 */
@Configuration
@Import({ApplicationConfig.class})
public class MicroAppConfig {

    /** Domain 層のコンテナ管理を表現します。 */
    @Configuration
    static class DomainAutoConfig {
        
        /** データ生成ユーティリティ */
        @Bean
        @ConditionalOnProperty(prefix = DataFixtures.Prefix, name = "enabled", matchIfMissing = false)
        DataFixtures fixtures() {
            return new DataFixtures();
        }
        
        /** 休日情報アクセサ */
        @Bean
        HolidayAccessor holidayAccessor(
                DefaultRepository rep,
                @Qualifier(DefaultRepository.BeanNameTx)
                PlatformTransactionManager txm) {
            return new HolidayAccessor(txm, rep); //low: 定義側にスキーマ指定を委ねるときは外部から設定するアプローチで
        }
        
        /** 営業日ユーティリティ */
        @Bean
        BusinessDayHandler businessDayHandler(Timestamper time, HolidayAccessor holiday) {
            return BusinessDayHandler.of(time, holiday);
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
