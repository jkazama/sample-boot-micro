package sample.microasset.model.asset;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Test;

import sample.EntityTestSupport;
import sample.microasset.model.asset.CashBalance;

//low: 簡易な正常系検証のみ
public class CashBalanceTest extends EntityTestSupport {

    @Override
    protected void setupPreset() {
        targetEntities(CashBalance.class);
    }

    @Test
    public void 現金残高を追加する() {
        LocalDate baseDay = businessDay.day();
        txAsset(() -> {
            CashBalance cb = fixturesAsset.cb("test1", baseDay, "USD", "10.02").save(repAsset);

            // 10.02 + 11.51 = 21.53
            assertThat(cb.add(repAsset, new BigDecimal("11.51")).getAmount(), is(new BigDecimal("21.53")));

            // 21.53 + 11.516 = 33.04 (端数切捨確認)
            assertThat(cb.add(repAsset, new BigDecimal("11.516")).getAmount(), is(new BigDecimal("33.04")));

            // 33.04 - 41.51 = -8.47 (マイナス値/マイナス残許容)
            assertThat(cb.add(repAsset, new BigDecimal("-41.51")).getAmount(), is(new BigDecimal("-8.47")));
        });
    }

    @Test
    public void 現金残高を取得する() {
        LocalDate baseDay = businessDay.day();
        LocalDate baseMinus1Day = businessDay.day(-1);
        txAsset(() -> {
            fixturesAsset.cb("test1", baseDay, "JPY", "1000").save(repAsset);
            fixturesAsset.cb("test2", baseMinus1Day, "JPY", "3000").save(repAsset);

            // 存在している残高の検証
            CashBalance cbNormal = CashBalance.getOrNew(repAsset, "test1", "JPY");
            assertThat(cbNormal, allOf(
                    hasProperty("accountId", is("test1")),
                    hasProperty("baseDay", is(baseDay)),
                    hasProperty("amount", is(new BigDecimal("1000")))));

            // 基準日に存在していない残高の繰越検証
            CashBalance cbRoll = CashBalance.getOrNew(repAsset, "test2", "JPY");
            assertThat(cbRoll, allOf(
                    hasProperty("accountId", is("test2")),
                    hasProperty("baseDay", is(baseDay)),
                    hasProperty("amount", is(new BigDecimal("3000")))));

            // 残高を保有しない口座の生成検証
            CashBalance cbNew = CashBalance.getOrNew(repAsset, "test3", "JPY");
            assertThat(cbNew, allOf(
                    hasProperty("accountId", is("test3")),
                    hasProperty("baseDay", is(baseDay)),
                    hasProperty("amount", is(BigDecimal.ZERO))));
        });
    }
}
