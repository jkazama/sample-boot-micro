package sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

/**
 * アプリケーションにおけるBean定義を表現します。
 * <p>クラス側でコンポーネント定義していない時はこちらで明示的に記載してください。
 */
@Configuration
public class ApplicationConfig {
    
    /** SpringMvcの拡張コンフィギュレーション */
    @Configuration
    static class WebMvcConfig extends WebMvcConfigurerAdapter {
        @Autowired
        private MessageSource message;

        /** HibernateのLazyLoading回避対応。  see JacksonAutoConfiguration */
        @Bean
        Hibernate5Module jsonHibernate5Module() {
            return new Hibernate5Module();
        }

        /** BeanValidationメッセージのUTF-8に対応したValidator。 */
        @Bean
        LocalValidatorFactoryBean validator() {
            LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
            factory.setValidationMessageSource(message);
            return factory;
        }

        /** 標準Validatorの差し替えをします。 */
        @Override
        public org.springframework.validation.Validator getValidator() {
            return validator();
        }

    }

}
