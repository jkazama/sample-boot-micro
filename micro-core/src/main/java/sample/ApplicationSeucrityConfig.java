package sample;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.*;
import org.springframework.web.filter.CorsFilter;

import sample.context.security.SecurityProperties;

/**
 * アプリケーションのセキュリティ定義を表現します。
 */
@Configuration
@EnableConfigurationProperties({ SecurityProperties.class })
public class ApplicationSeucrityConfig {
    
    /** パスワード用のハッシュ(BCrypt)エンコーダー。 */
    @Bean
    PasswordEncoder passwordEncoder() {
        //low: きちんとやるのであれば、strengthやSecureRandom使うなど外部切り出し含めて検討してください
        return new BCryptPasswordEncoder();
    }

    /** CORS全体適用 */
    @Bean
    @ConditionalOnProperty(prefix = "extension.security.cors", name = "enabled", matchIfMissing = false)
    CorsFilter corsFilter(SecurityProperties props) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(props.cors().isAllowCredentials());
        config.addAllowedOrigin(props.cors().getAllowedOrigin());
        config.addAllowedHeader(props.cors().getAllowedHeader());
        config.addAllowedMethod(props.cors().getAllowedMethod());
        config.setMaxAge(props.cors().getMaxAge());
        source.registerCorsConfiguration(props.cors().getPath(), config);
        return new CorsFilter(source);
    }
}
