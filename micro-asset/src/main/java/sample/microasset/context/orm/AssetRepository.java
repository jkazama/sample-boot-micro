package sample.microasset.context.orm;

import javax.persistence.*;
import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.orm.jpa.*;

import lombok.*;
import sample.context.orm.*;

/** 資産スキーマのRepositoryを表現します。 */
@Setter
public class AssetRepository extends OrmRepository {
    public static final String BeanNameDs = "assetDataSource";
    public static final String BeanNameEmf = "assetEntityManagerFactory";
    public static final String BeanNameTx = "assetTransactionManager";

    @PersistenceContext(unitName = BeanNameEmf)
    private EntityManager em;

    @Override
    public EntityManager em() {
        return em;
    }

    /** 資産スキーマのHibernateコンポーネントを生成します。 */
    @ConfigurationProperties(prefix = "extension.datasource.asset")
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class AssetDataSourceProperties extends OrmDataSourceProperties {
        private OrmRepositoryProperties jpa = new OrmRepositoryProperties();
        
        public DataSource dataSource() {
            return super.dataSource();
        }
        
        public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(
                final DataSource dataSource) {
            return jpa.entityManagerFactoryBean(BeanNameEmf, dataSource);
        }

        public JpaTransactionManager transactionManager(final EntityManagerFactory emf) {
            return jpa.transactionManager(emf);
        }
    }

}
