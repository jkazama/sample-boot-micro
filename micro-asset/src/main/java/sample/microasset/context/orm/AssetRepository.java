package sample.microasset.context.orm;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate5.*;
import org.springframework.stereotype.Repository;

import lombok.Setter;
import sample.context.orm.*;

/** 資産スキーマのRepositoryを表現します。 */
@Repository
@Setter
public class AssetRepository extends OrmRepository {
    public static final String Prefix = "extension.datasource.asset";
    
    public static final String BeanNameDs = "assetDataSource";
    public static final String BeanNameSf = "assetSessionFactory";
    public static final String BeanNameTx = "assetTransactionManager";

    @Autowired
    @Qualifier(BeanNameSf)
    private SessionFactory sessionFactory;

    @Override
    public SessionFactory sf() {
        return sessionFactory;
    }

    /** 資産スキーマのHibernateコンポーネントを生成します。 */
    @ConfigurationProperties(prefix = "extension.hibernate.asset")
    public static class AssetRepositoryConfig extends OrmRepositoryConfig {
        @Bean(name = BeanNameSf)
        @Override
        public LocalSessionFactoryBean sessionFactory(
                @Qualifier(BeanNameDs) final DataSource dataSource, final OrmInterceptor interceptor) {
            return super.sessionFactory(dataSource, interceptor);
        }

        @Bean(name = BeanNameTx)
        @Override
        public HibernateTransactionManager transactionManager(
                @Qualifier(BeanNameSf) final SessionFactory sessionFactory) {
            return super.transactionManager(sessionFactory);
        }
    }

    /** 資産スキーマのDataSourceを生成します。 */
    @ConfigurationProperties(prefix = Prefix)
    public static class AssetDataSourceConfig extends OrmDataSourceConfig {
        @Bean(name = BeanNameDs, destroyMethod = "shutdown")
        public DataSource dataSource() {
            return super.dataSource();
        }
    }

}
