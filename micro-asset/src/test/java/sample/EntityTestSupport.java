package sample;

import java.io.IOException;
import java.time.Clock;
import java.util.*;
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.junit.*;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import sample.context.*;
import sample.context.actor.ActorSession;
import sample.context.orm.*;
import sample.context.orm.DefaultRepository.DefaultRepositoryConfig;
import sample.microasset.context.orm.*;
import sample.microasset.model.AssetDataFixtures;
import sample.model.*;
import sample.support.MockDomainHelper;

/**
 * Springコンテナを用いないHibernateのみに特化した検証用途。
 * <p>modelパッケージでのみ利用してください。
 */
public class EntityTestSupport {
    protected Clock clock = Clock.systemDefaultZone();
    protected Timestamper time;
    protected BusinessDayHandler businessDay;
    protected PasswordEncoder encoder;
    protected ActorSession session;
    protected MockDomainHelper dh;
    protected SessionFactory sf;
    protected DefaultRepository rep;
    protected PlatformTransactionManager txm;
    protected SystemRepository repSystem;
    protected PlatformTransactionManager txmSystem;
    protected AssetRepository repAsset;
    protected PlatformTransactionManager txmAsset;
    protected DataFixtures fixtures;
    protected AssetDataFixtures fixturesAsset;

    /** テスト対象とするパッケージパス(通常はtargetEntitiesの定義を推奨) */
    private String packageToScan = "sample";
    /** テスト対象とするEntityクラス一覧 */
    private List<Class<?>> targetEntities = new ArrayList<>();

    @Before
    public final void setup() {
        setupPreset();
        dh = new MockDomainHelper(clock);
        time = dh.time();
        session = dh.actorSession();
        businessDay = new BusinessDayHandler(time, null);
        encoder = new BCryptPasswordEncoder();
        setupRepository();
        setupDataFixtures();
        before();
    }

    /** 設定事前処理。repインスタンス生成前 */
    protected void setupPreset() {
        // 各Entity検証で上書きしてください
    }

    /** 事前処理。repインスタンス生成後 */
    protected void before() {
        // 各Entity検証で上書きしてください
    }

    /**
     * {@link #setupPreset()}内で対象Entityを指定してください。
     * (targetEntitiesといずれかを設定する必要があります)
     */
    protected void targetPackage(String packageToScan) {
        this.packageToScan = packageToScan;
    }

    /**
     * {@link #setupPreset()}内で対象Entityを指定してください。
     * (targetPackageといずれかを設定する必要があります)
     */
    protected void targetEntities(Class<?>... list) {
        if (list != null) {
            this.targetEntities = Arrays.asList(list);
        }
    }

    /**
     * {@link #setupPreset()}内で利用したいClockを指定してください。
     */
    protected void clock(Clock clock) {
        this.clock = clock;
    }

    /**
     * {@link #before()}内でモック設定値を指定してください。
     */
    protected void setting(String id, String value) {
        dh.setting(id, value);
    }

    @After
    public void cleanup() {
        sf.close();
    }

    protected void setupRepository() {
        SessionFactory sf = createSessionFactory();
        rep = new DefaultRepository();
        rep.setDh(dh);
        rep.setSessionFactory(sf);
        repSystem = new SystemRepository();
        repSystem.setDh(dh);
        repSystem.setSessionFactory(sf);
        repAsset = new AssetRepository();
        repAsset.setDh(dh);
        repAsset.setSessionFactory(sf);
    }

    protected void setupDataFixtures() {
        fixtures = new DataFixtures();
        fixtures.setEncoder(encoder);
        fixtures.setRep(rep);
        fixtures.setTx(txm);
        fixtures.setRepSystem(repSystem);
        fixtures.setTxSystem(txmSystem);
        
        fixturesAsset = new AssetDataFixtures();
        fixturesAsset.setTime(time);
        fixturesAsset.setBusinessDay(businessDay);
        fixturesAsset.setRep(repAsset);
        fixturesAsset.setTx(txmAsset);
    }

    private SessionFactory createSessionFactory() {
        DataSource ds = EntityTestFactory.dataSource();
        DefaultRepositoryConfig config = new DefaultRepository.DefaultRepositoryConfig();
        config.setShowSql(true);
        config.setCreateDrop(true);
        if (targetEntities.isEmpty()) {
            config.setPackageToScan(packageToScan);
        } else {
            config.setAnnotatedClasses(targetEntities.toArray(new Class[0]));
        }
        OrmInterceptor interceptor = new OrmInterceptor();
        interceptor.setTime(time);
        interceptor.setSession(session);
        LocalSessionFactoryBean sfBean = config.sessionFactory(ds, interceptor);
        try {
            sfBean.afterPropertiesSet();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        sf = sfBean.getObject();
        txm = config.transactionManager(sf);
        txmSystem = config.transactionManager(sf);
        txmAsset = config.transactionManager(sf);
        return sf;
    }

    // 簡易コンポーネントFactory
    public static class EntityTestFactory {
        private static Optional<DataSource> ds = Optional.empty();

        static synchronized DataSource dataSource() {
            return ds.orElseGet(() -> {
                ds = Optional.of(createDataSource());
                return ds.get();
            });
        }

        private static DataSource createDataSource() {
            OrmDataSourceConfig ds = new OrmDataSourceConfig();
            ds.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
            ds.setUsername("");
            ds.setPassword("");
            return ds.dataSource();
        }
    }

    /** トランザクション処理を行います。 */
    public <T> T tx(Supplier<T> callable) {
        return new TransactionTemplate(txm).execute((status) -> {
            T ret = callable.get();
            if (ret instanceof Entity) {
                ret.hashCode(); // for lazy loading
            }
            return ret;
        });
    }

    public void tx(Runnable command) {
        tx(() -> {
            command.run();
            return true;
        });
    }

    /** トランザクション処理を行います。(System) */
    public <T> T txSystem(Supplier<T> callable) {
        return new TransactionTemplate(txmSystem).execute((status) -> {
            T ret = callable.get();
            if (ret instanceof Entity) {
                ret.hashCode(); // for lazy loading
            }
            return ret;
        });
    }

    public void txSystem(Runnable command) {
        txSystem(() -> {
            command.run();
            return true;
        });
    }
    
    /** トランザクション処理を行います。(Asset) */
    public <T> T txAsset(Supplier<T> callable) {
        return new TransactionTemplate(txmAsset).execute((status) -> {
            T ret = callable.get();
            if (ret instanceof Entity) {
                ret.hashCode(); // for lazy loading
            }
            return ret;
        });
    }

    public void txAsset(Runnable command) {
        txAsset(() -> {
            command.run();
            return true;
        });
    }
}
