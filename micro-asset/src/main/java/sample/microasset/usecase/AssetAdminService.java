package sample.microasset.usecase;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import sample.context.lock.IdLockHandler.LockType;
import sample.microasset.context.orm.AssetRepository;
import sample.microasset.model.asset.*;
import sample.microasset.model.asset.CashInOut.FindCashInOut;
import sample.usecase.*;

/**
 * 資産ドメインに対する社内ユースケース処理。
 */
@Service
public class AssetAdminService extends ServiceSupport {
    
    @Autowired
    AssetRepository repAsset;
    @Autowired
    @Qualifier(AssetRepository.BeanNameTx)
    private PlatformTransactionManager txAsset;
    
    /**
     * 振込入出金依頼を検索します。
     * low: 口座横断的なので割り切りでREADロックはかけません。
     */
    @Transactional(AssetRepository.BeanNameTx)
    public List<CashInOut> findCashInOut(final FindCashInOut p) {
        return CashInOut.find(repAsset, p);
    }

    /**
     * 振込出金依頼を締めます。
     */
    public void closingCashOut() {
        audit.audit("振込出金依頼の締め処理をする", () -> txAsset(() -> closingCashOutInTx()));
    }

    private void closingCashOutInTx() {
        //low: 以降の処理は口座単位でfilter束ねしてから実行する方が望ましい。
        //low: 大量件数の処理が必要な時はそのままやるとヒープが死ぬため、idソートでページング分割して差分実行していく。
        CashInOut.findUnprocessed(repAsset).forEach(cio -> {
            //low: TX内のロックが適切に動くかはIdLockHandlerの実装次第。
            // 調整が難しいようなら大人しく営業停止時間(IdLock必要な処理のみ非活性化されている状態)を作って、
            // ロック無しで一気に処理してしまう方がシンプル。
            idLock.call(cio.getAccountId(), LockType.Write, () -> {
                try {
                    cio.process(repAsset);
                    //low: SQLの発行担保。扱う情報に相互依存が無く、セッションキャッシュはリークしがちなので都度消しておく。
                    rep.flushAndClear();
                } catch (Exception e) {
                    logger.error("[" + cio.getId() + "] 振込出金依頼の締め処理に失敗しました。", e);
                    try {
                        cio.error(repAsset);
                        rep.flush();
                    } catch (Exception ex) {
                        //low: 2重障害(恐らくDB起因)なのでloggerのみの記載に留める
                    }
                }
            });
        });
    }

    /**
     * キャッシュフローを実現します。
     * <p>受渡日を迎えたキャッシュフローを残高に反映します。
     */
    public void realizeCashflow() {
        audit.audit("キャッシュフローを実現する", () -> txAsset(() -> realizeCashflowInTx()));
    }

    private void realizeCashflowInTx() {
        //low: 日回し後の実行を想定
        LocalDate day = dh.time().day();
        for (final Cashflow cf : Cashflow.findDoRealize(repAsset, day)) {
            idLock.call(cf.getAccountId(), LockType.Write, () -> {
                try {
                    cf.realize(repAsset);
                    rep.flushAndClear();
                } catch (Exception e) {
                    logger.error("[" + cf.getId() + "] キャッシュフローの実現に失敗しました。", e);
                    try {
                        cf.error(repAsset);
                        rep.flush();
                    } catch (Exception ex) {
                    }
                }
            });
        }
    }
    
    /** トランザクション処理を実行します。 */
    private void txAsset(Runnable command) {
        ServiceUtils.tx(txAsset, command);
    }
    
}
