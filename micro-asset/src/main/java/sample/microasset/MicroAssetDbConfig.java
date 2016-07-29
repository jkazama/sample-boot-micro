package sample.microasset;

import static sample.microasset.context.orm.AssetRepository.*;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.orm.jpa.*;

import sample.microasset.context.orm.AssetRepository;
import sample.microasset.context.orm.AssetRepository.AssetDataSourceProperties;

/**
 * 資産ドメインのデータベース接続定義を表現します。
 */
@Configuration
@EnableConfigurationProperties({AssetDataSourceProperties.class})
public class MicroAssetDbConfig {
    
    @Bean
    @DependsOn(BeanNameEmf)
    AssetRepository assetRepository() {
        return new AssetRepository();
    }
    
    @Bean(name = BeanNameDs, destroyMethod = "close")
    DataSource assetDataSource(AssetDataSourceProperties props) {
        return props.dataSource();
    }
    
    @Bean(name = BeanNameEmf)
    LocalContainerEntityManagerFactoryBean assetEntityManagerFactoryBean(
            AssetDataSourceProperties props,
            @Qualifier(AssetRepository.BeanNameDs) final DataSource dataSource) {
        return props.entityManagerFactoryBean(dataSource);
    }

    @Bean(name = BeanNameTx)
    JpaTransactionManager assetTransactionManager(
            AssetDataSourceProperties props,
            @Qualifier(AssetRepository.BeanNameEmf) final EntityManagerFactory emf) {
        return props.transactionManager(emf);
    }

}
