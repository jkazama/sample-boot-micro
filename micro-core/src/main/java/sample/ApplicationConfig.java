package sample;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

import sample.context.*;
import sample.context.actor.ActorSession;
import sample.context.audit.AuditHandler;
import sample.context.audit.AuditHandler.AuditPersister;
import sample.context.lock.IdLockHandler;
import sample.context.mail.MailHandler;
import sample.context.report.ReportHandler;
import sample.controller.*;

/**
 * アプリケーションにおける汎用 Bean 定義を表現します。
 * <p>クラス側でコンポーネント定義していない時はこちらで明示的に記載してください。
 */
@Configuration
@Import({ApplicationDbConfig.class, ApplicationSecurityConfig.class})
public class ApplicationConfig {
    
    /** SpringMvcの拡張コンフィギュレーション */
    @Configuration
    static class WebMvcConfig {

        /** HibernateのLazyLoading回避対応。  see JacksonAutoConfiguration */
        @Bean
        Hibernate5Module jsonHibernate5Module() {
            return new Hibernate5Module();
        }

        /** BeanValidationメッセージのUTF-8に対応したValidator。 */
        @Bean
        LocalValidatorFactoryBean defaultValidator(MessageSource message) {
            LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
            factory.setValidationMessageSource(message);
            return factory;
        }

    }

    /** インフラ層 ( context 配下) のコンポーネント定義を表現します */
    @Configuration
    static class PlainConfig {
        @Bean
        Timestamper timestamper() {
            return new Timestamper();
        }
        @Bean
        ActorSession actorSession() {
            return new ActorSession();
        }
        @Bean
        ResourceBundleHandler resourceBundleHandler() {
            return new ResourceBundleHandler();
        }
        @Bean
        AppSettingHandler appSettingHandler() {
            return new AppSettingHandler();
        }
        @Bean
        AuditHandler auditHandler() {
            return new AuditHandler();
        }
        @Bean
        AuditPersister auditPersister() {
            return new AuditPersister();
        }
        @Bean
        IdLockHandler idLockHandler() {
            return new IdLockHandler();
        }
        @Bean
        MailHandler mailHandler() {
            return new MailHandler();
        }
        @Bean
        ReportHandler reportHandler() {
            return new ReportHandler();
        }
        @Bean
        DomainHelper domainHelper() {
            return new DomainHelper();
        }
    }
    
    /** API ( json 形式 ) サポート */
    @Configuration
    @Import({RestErrorAdvice.class, RestErrorController.class})
    static class ApiConfig {
    }

}
