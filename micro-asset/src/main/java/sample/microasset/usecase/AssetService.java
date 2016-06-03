package sample.microasset.usecase;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import sample.context.actor.Actor;
import sample.context.lock.IdLockHandler.LockType;
import sample.microasset.context.orm.AssetRepository;
import sample.microasset.model.asset.CashInOut;
import sample.microasset.model.asset.CashInOut.RegCashOut;
import sample.microasset.usecase.mail.AssetMailDeliver;
import sample.usecase.*;

/**
 * 資産ドメインに対する顧客ユースケース処理。
 */
@Service
public class AssetService extends ServiceSupport {

    @Autowired
    AssetRepository repAsset;
    @Autowired
    @Qualifier(AssetRepository.BeanNameTx)
    private PlatformTransactionManager txAsset;
    @Autowired
    AssetMailDeliver mail;
    
    /** 匿名を除くActorを返します。 */
    @Override
    protected Actor actor() {
        return ServiceUtils.actorUser(super.actor());
    }

    /**
     * 未処理の振込依頼情報を検索します。
     * low: 参照系は口座ロックが必要無いケースであれば@Transactionalでも十分
     * low: CashInOutは情報過多ですがアプリケーション層では公開対象を特定しにくい事もあり、
     * UI層に最終判断を委ねています。
     */
    public List<CashInOut> findUnprocessedCashOut() {
        final String accId = actor().getId();
        return txAsset(accId, LockType.Read, () -> {
            return CashInOut.findUnprocessed(repAsset, accId);
        });
    }

    /**
     * 振込出金依頼をします。
     * low: 公開リスクがあるためUI層には必要以上の情報を返さない事を意識します。
     * low: 監査ログの記録は状態を変えうる更新系ユースケースでのみ行います。
     * low: ロールバック発生時にメールが飛ばないようにトランザクション境界線を明確に分離します。
     * @return 振込出金依頼ID
     */
    public Long withdraw(final RegCashOut p) {
        return audit.audit("振込出金依頼をします", () -> {
            p.setAccountId(actor().getId()); // 顧客側はログイン利用者で強制上書き
            // low: 口座IDロック(WRITE)とトランザクションをかけて振込処理
            CashInOut cio = txAsset(actor().getId(), LockType.Write, () -> {
                return tx(() -> CashInOut.withdraw(repAsset, rep, businessDay(), p));
            });
            // low: トランザクション確定後に出金依頼を受付した事をメール通知します。
            mail.sendWithdrawal(cio);
            return cio.getId();
        });
    }

    /** 口座ロック付でトランザクション処理を実行します。 */
    private <T> T txAsset(String accountId, LockType lockType, final Supplier<T> callable) {
        return idLock.call(accountId, lockType, () -> {
            return txAsset(callable);
        });
    }
    
    /** トランザクション処理を実行します。 */
    private <T> T txAsset(Supplier<T> callable) {
        return ServiceUtils.tx(txAsset, callable);
    }

}
