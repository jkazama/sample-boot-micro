package sample.microasset.usecase;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import sample.UnitTestSupport;
import sample.microasset.model.asset.*;

/**
 * AssetService の単体検証です。
 * <p>low: 簡易な正常系検証が中心
 */
public class AssetServiceTest extends UnitTestSupport {

    private static final String ccy = "JPY";
    private static final String accId = "test";
    
    @Autowired
    private AssetService service;

    @Override
    public void before() {
        // 残高1000円の口座(test)を用意
        LocalDate baseDay = businessDay.day();
        tx(() -> {
            fixtures.selfFiAcc(Remarks.CashOut, ccy).save(rep);
            fixtures.acc(accId).save(rep);
            fixtures.fiAcc(accId, Remarks.CashOut, ccy).save(rep);
        });
        txAsset(() -> {
            fixturesAsset.cb(accId, baseDay, ccy, "1000").save(repAsset);
            fixturesAsset.cio(accId, "1000", true).save(repAsset);
        });
        
        loginUser(accId);
    }
    
    @Test
    public void 未処理の振込依頼情報を検索します() {
        List<CashInOut> list = service.findUnprocessedCashOut();
        assertFalse(list.isEmpty());
        CashInOut cio = list.get(0);
        assertThat(cio, allOf(
                hasProperty("accountId", is(accId)),
                hasProperty("currency", is(ccy)),
                hasProperty("withdrawal", is(true))));
        assertEquals(new BigDecimal("1000"), cio.getAbsAmount().setScale(0));
    }
    
}
