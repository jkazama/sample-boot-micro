package sample.microasset.model.asset;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Test;

import sample.*;
import sample.ValidationException.ErrorKeys;
import sample.microasset.model.asset.CashInOut.*;
import sample.microasset.model.asset.type.CashflowType;
import sample.model.DomainErrorKeys;
import sample.model.account.*;
import sample.model.master.SelfFiAccount;

//low: 簡易な正常系検証が中心。依存するCashflow/CashBalanceの単体検証パスを前提。
public class CashInOutTest extends EntityTestSupport {

    private static final String ccy = "JPY";
    private static final String accId = "test";

    @Override
    protected void setupPreset() {
        targetEntities(Account.class, FiAccount.class, SelfFiAccount.class,
                CashInOut.class, Cashflow.class, CashBalance.class);
    }

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
        });
    }

    @Test
    public void 振込入出金を検索する() {
        LocalDate baseDay = businessDay.day();
        LocalDate basePlus1Day = businessDay.day(1);
        LocalDate basePlus2Day = businessDay.day(2);
        txAsset(() -> {
            fixturesAsset.cio(accId, "300", true).save(rep);
            //low: ちゃんとやると大変なので最低限の検証
            assertThat(
                    CashInOut.find(repAsset, findParam(baseDay, basePlus1Day)),
                    hasSize(1));
            assertThat(
                    CashInOut.find(repAsset, findParam(baseDay, basePlus1Day, ActionStatusType.Unprocessed)),
                    hasSize(1));
            assertThat(
                    CashInOut.find(repAsset, findParam(baseDay, basePlus1Day, ActionStatusType.Processed)),
                    empty());
            assertThat(
                    CashInOut.find(repAsset, findParam(basePlus1Day, basePlus2Day, ActionStatusType.Unprocessed)),
                    empty());
        });
    }

    private FindCashInOut findParam(LocalDate fromDay, LocalDate toDay, ActionStatusType... statusTypes) {
        return new FindCashInOut(ccy, statusTypes, fromDay, toDay);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void 振込出金依頼をする() {
        LocalDate baseDay = businessDay.day();
        LocalDate basePlus3Day = businessDay.day(3);
        txAsset(() -> {
            // 超過の出金依頼 [例外]
            try {
                CashInOut.withdraw(repAsset, rep, businessDay, new RegCashOut(accId, ccy, new BigDecimal("1001")));
                fail();
            } catch (ValidationException e) {
                assertThat(e.getMessage(), is(AssetErrorKeys.CashInOutWithdrawAmount));
            }

            // 0円出金の出金依頼 [例外]
            try {
                CashInOut.withdraw(repAsset, rep, businessDay, new RegCashOut(accId, ccy, BigDecimal.ZERO));
                fail();
            } catch (ValidationException e) {
                assertThat(e.getMessage(), is(DomainErrorKeys.AbsAmountZero));
            }

            // 通常の出金依頼
            CashInOut normal = CashInOut.withdraw(repAsset, rep, businessDay, new RegCashOut(accId, ccy, new BigDecimal("300")));
            assertThat(normal, allOf(
                    hasProperty("accountId", is(accId)), hasProperty("currency", is(ccy)),
                    hasProperty("absAmount", is(new BigDecimal(300))), hasProperty("withdrawal", is(true)),
                    hasProperty("requestDay", is(baseDay)),
                    hasProperty("eventDay", is(baseDay)),
                    hasProperty("valueDay", is(basePlus3Day)),
                    hasProperty("targetFiCode", is(Remarks.CashOut + "-" + ccy)),
                    hasProperty("targetFiAccountId", is("FI" + accId)),
                    hasProperty("selfFiCode", is(Remarks.CashOut + "-" + ccy)),
                    hasProperty("selfFiAccountId", is("xxxxxx")),
                    hasProperty("statusType", is(ActionStatusType.Unprocessed)),
                    hasProperty("cashflowId", is(nullValue()))));

            // 拘束額を考慮した出金依頼 [例外]
            try {
                CashInOut.withdraw(repAsset, rep, businessDay, new RegCashOut(accId, ccy, new BigDecimal("701")));
                fail();
            } catch (ValidationException e) {
                assertThat(e.getMessage(), is(AssetErrorKeys.CashInOutWithdrawAmount));
            }
        });
    }

    @Test
    public void 振込出金依頼を取消する() {
        LocalDate baseDay = businessDay.day();
        txAsset(() -> {
            // CF未発生の依頼を取消
            CashInOut normal = fixturesAsset.cio(accId, "300", true).save(repAsset);
            assertThat(normal.cancel(repAsset), hasProperty("statusType", is(ActionStatusType.Cancelled)));

            // 発生日を迎えた場合は取消できない [例外]
            CashInOut today = fixturesAsset.cio(accId, "300", true);
            today.setEventDay(baseDay);
            today.save(repAsset);
            try {
                today.cancel(repAsset);
                fail();
            } catch (ValidationException e) {
                assertThat(e.getMessage(), is(AssetErrorKeys.CashInOutBeforeEqualsDay));
            }
        });
    }

    @Test
    public void 振込出金依頼を例外状態とする() {
        LocalDate baseDay = businessDay.day();
        txAsset(() -> {
            CashInOut normal = fixturesAsset.cio(accId, "300", true).save(repAsset);
            assertThat(normal.error(repAsset), hasProperty("statusType", is(ActionStatusType.Error)));

            // 処理済の時はエラーにできない [例外]
            CashInOut today = fixturesAsset.cio(accId, "300", true);
            today.setEventDay(baseDay);
            today.setStatusType(ActionStatusType.Processed);
            today.save(repAsset);
            try {
                today.error(repAsset);
                fail();
            } catch (ValidationException e) {
                assertThat(e.getMessage(), is(ErrorKeys.ActionUnprocessing));
            }
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void 発生日を迎えた振込入出金をキャッシュフロー登録する() {
        LocalDate baseDay = businessDay.day();
        LocalDate basePlus3Day = businessDay.day(3);
        txAsset(() -> {
            // 発生日未到来の処理 [例外]
            CashInOut future = fixturesAsset.cio(accId, "300", true).save(repAsset);
            try {
                future.process(repAsset);
                fail();
            } catch (ValidationException e) {
                assertThat(e.getMessage(), is(AssetErrorKeys.CashInOutAfterEqualsDay));
            }

            // 発生日到来処理
            CashInOut normal = fixturesAsset.cio(accId, "300", true);
            normal.setEventDay(baseDay);
            normal.save(repAsset);
            assertThat(normal.process(repAsset), allOf(
                    hasProperty("statusType", is(ActionStatusType.Processed)),
                    hasProperty("cashflowId", not(nullValue()))));
            // 発生させたキャッシュフローの検証
            assertThat(Cashflow.load(repAsset, normal.getCashflowId()), allOf(
                    hasProperty("accountId", is(accId)),
                    hasProperty("currency", is(ccy)),
                    hasProperty("amount", is(new BigDecimal("-300"))),
                    hasProperty("cashflowType", is(CashflowType.CashOut)),
                    hasProperty("remark", is(Remarks.CashOut)),
                    hasProperty("eventDay", is(baseDay)),
                    hasProperty("valueDay", is(basePlus3Day)),
                    hasProperty("statusType", is(ActionStatusType.Unprocessed))));
        });
    }

}
