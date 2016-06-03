package sample.model;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.Setter;
import sample.context.*;
import sample.context.orm.*;
import sample.model.account.*;
import sample.model.account.type.AccountStatusType;
import sample.model.master.*;
import sample.util.DateUtils;

/**
 * データ生成用のサポートコンポーネント。
 * <p>テストや開発時の簡易マスタデータ生成を目的としているため本番での利用は想定していません。
 */
@Setter
public class DataFixtures {
    
    public static final String Prefix = "extension.datafixture";

    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private DefaultRepository rep;
    @Autowired
    @Qualifier(DefaultRepository.BeanNameTx)
    private PlatformTransactionManager tx;
    @Autowired
    private SystemRepository repSystem;
    @Autowired
    @Qualifier(SystemRepository.BeanNameTx)
    private PlatformTransactionManager txSystem;

    @PostConstruct
    public void initialize() {
        new TransactionTemplate(txSystem).execute((status) -> {
            initializeInTxSystem();
            return true;
        });
        new TransactionTemplate(tx).execute((status) -> {
            initializeInTx();
            return true;
        });
    }

    public void initializeInTxSystem() {
        String day = DateUtils.dayFormat(LocalDate.now());
        new AppSetting(Timestamper.KeyDay, "system", "営業日", day).save(repSystem);
    }

    public void initializeInTx() {
        String ccy = "JPY";

        // 社員: admin (passも同様)
        staff("admin").save(rep);

        // 自社金融機関
        selfFiAcc("cashOut", ccy).save(rep);

        // 口座: sample (passも同様)
        String idSample = "sample";
        acc(idSample).save(rep);
        login(idSample).save(rep);
        fiAcc(idSample, "cashOut", ccy).save(rep);
    }

    // account

    /** 口座の簡易生成 */
    public Account acc(String id) {
        Account m = new Account();
        m.setId(id);
        m.setName(id);
        m.setMail("hoge@example.com");
        m.setStatusType(AccountStatusType.Normal);
        return m;
    }

    public Login login(String id) {
        Login m = new Login();
        m.setId(id);
        m.setLoginId(id);
        m.setPassword(encoder.encode(id));
        return m;
    }

    /** 口座に紐付く金融機関口座の簡易生成 */
    public FiAccount fiAcc(String accountId, String category, String currency) {
        FiAccount m = new FiAccount();
        m.setAccountId(accountId);
        m.setCategory(category);
        m.setCurrency(currency);
        m.setFiCode(category + "-" + currency);
        m.setFiAccountId("FI" + accountId);
        return m;
    }

    // master

    /** 社員の簡易生成 */
    public Staff staff(String id) {
        Staff m = new Staff();
        m.setId(id);
        m.setName(id);
        m.setPassword(encoder.encode(id));
        return m;
    }

    /** 社員権限の簡易生成 */
    public List<StaffAuthority> staffAuth(String id, String... authority) {
        return Arrays.stream(authority).map((auth) -> new StaffAuthority(null, id, auth)).collect(Collectors.toList());
    }

    /** 自社金融機関口座の簡易生成 */
    public SelfFiAccount selfFiAcc(String category, String currency) {
        SelfFiAccount m = new SelfFiAccount();
        m.setCategory(category);
        m.setCurrency(currency);
        m.setFiCode(category + "-" + currency);
        m.setFiAccountId("xxxxxx");
        return m;
    }

    /** 祝日の簡易生成 */
    public Holiday holiday(String dayStr) {
        Holiday m = new Holiday();
        m.setCategory(Holiday.CategoryDefault);
        m.setName("休日サンプル");
        m.setDay(DateUtils.day(dayStr));
        return m;
    }

}
