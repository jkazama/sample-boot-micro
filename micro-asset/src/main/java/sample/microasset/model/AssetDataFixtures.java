package sample.microasset.model;

import java.math.BigDecimal;
import java.time.*;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.Setter;
import sample.ActionStatusType;
import sample.context.Timestamper;
import sample.microasset.context.orm.AssetRepository;
import sample.microasset.model.asset.*;
import sample.microasset.model.asset.Cashflow.RegCashflow;
import sample.microasset.model.asset.type.CashflowType;
import sample.model.BusinessDayHandler;
import sample.util.TimePoint;

@Setter
public class AssetDataFixtures {
    
    @Autowired
    private Timestamper time;
    @Autowired
    private BusinessDayHandler businessDay;
    @Autowired
    private AssetRepository rep;
    @Autowired
    @Qualifier(AssetRepository.BeanNameTx)
    private PlatformTransactionManager tx;
    
    @PostConstruct
    public void initialize() {
        new TransactionTemplate(tx).execute((status) -> {
            initializeInTx();
            return true;
        });
    }
    
    public void initializeInTx() {
        String ccy = "JPY";
        LocalDate baseDay = businessDay.day();

        // 口座資産: sample
        cb("sample", baseDay, ccy, "1000000").save(rep);
    }
    
    // asset

    /** 口座残高の簡易生成 */
    public CashBalance cb(String accountId, LocalDate baseDay, String currency, String amount) {
        return new CashBalance(null, accountId, baseDay, currency, new BigDecimal(amount), LocalDateTime.now());
    }

    /** キャッシュフローの簡易生成 */
    public Cashflow cf(String accountId, String amount, LocalDate eventDay, LocalDate valueDay) {
        return cfReg(accountId, amount, valueDay).create(TimePoint.of(eventDay));
    }

    /** キャッシュフロー登録パラメタの簡易生成 */
    public RegCashflow cfReg(String accountId, String amount, LocalDate valueDay) {
        return new RegCashflow(accountId, "JPY", new BigDecimal(amount), CashflowType.CashIn, "cashIn", null, valueDay);
    }

    /** 振込入出金依頼の簡易生成 [発生日(T+1)/受渡日(T+3)] */
    public CashInOut cio(String accountId, String absAmount, boolean withdrawal) {
        TimePoint now = time.tp();
        CashInOut m = new CashInOut();
        m.setAccountId(accountId);
        m.setCurrency("JPY");
        m.setAbsAmount(new BigDecimal(absAmount));
        m.setWithdrawal(withdrawal);
        m.setRequestDay(now.getDay());
        m.setRequestDate(now.getDate());
        m.setEventDay(businessDay.day(1));
        m.setValueDay(businessDay.day(3));
        m.setTargetFiCode("tFiCode");
        m.setTargetFiAccountId("tFiAccId");
        m.setSelfFiCode("sFiCode");
        m.setSelfFiAccountId("sFiAccId");
        m.setStatusType(ActionStatusType.Unprocessed);
        return m;
    }

}
