package sample.microasset.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import sample.context.actor.*;
import sample.context.audit.AuditHandler;
import sample.context.lock.IdLockHandler;
import sample.context.orm.*;
import sample.microasset.context.orm.AssetRepository;
import sample.microasset.model.asset.CashInOut;
import sample.microasset.model.asset.CashInOut.RegCashOut;
import sample.microasset.usecase.event.AppMailEvent;
import sample.microasset.usecase.event.AppMailEvent.AppMailType;
import sample.model.BusinessDayHandler;
import sample.usecase.ServiceUtils;

/**
 * 資産ドメインに対する顧客ユースケース処理。
 */
@Service
public class AssetService {

    private final AssetRepository rep;
    private final PlatformTransactionManager txm;
    private final DefaultRepository repAccount;
    private final PlatformTransactionManager txmAccount;
    private final ActorSession actorSession;
    private final AuditHandler audit;
    private final IdLockHandler idLock;
    private final BusinessDayHandler businessDay;
    private final ApplicationEventPublisher event;

    public AssetService(
            AssetRepository rep,
            @Qualifier(AssetRepository.BeanNameTx) PlatformTransactionManager txm,
            DefaultRepository repAccount,
            @Qualifier(DefaultRepository.BeanNameTx) PlatformTransactionManager txmAccount,
            ActorSession actorSession,
            AuditHandler audit,
            IdLockHandler idLock,
            BusinessDayHandler businessDay,
            ApplicationEventPublisher event) {
        this.rep = rep;
        this.txm = txm;
        this.repAccount = repAccount;
        this.txmAccount = txmAccount;
        this.actorSession = actorSession;
        this.audit = audit;
        this.idLock = idLock;
        this.businessDay = businessDay;
        this.event = event;
    }

    /**
     * 未処理の振込依頼情報を検索します。
     * low: 参照系は口座ロックが必要無いケースであれば@Transactionalでも十分
     * low: CashInOutは情報過多ですがアプリケーション層では公開対象を特定しにくい事もあり、
     * UI層に最終判断を委ねています。
     */
    public List<CashInOut> findUnprocessedCashOut() {
        final String accId = actor().getId();
        return TxTemplate.of(txm).readIdLock(idLock, accId).tx(() -> {
            return CashInOut.findUnprocessed(rep, accId);
        });
    }

    /** 匿名を除くActorを返します。 */
    private Actor actor() {
        return ServiceUtils.actorUser(actorSession.actor());
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
            CashInOut cio = TxTemplate.of(txm).writeIdLock(idLock, actor().getId())
                    .tx(() -> TxTemplate.of(txmAccount).tx(() -> {
                        return CashInOut.withdraw(rep, repAccount, businessDay, p);
                    }));
            // low: トランザクション確定後に出金依頼を受付した事をメール通知します。
            event.publishEvent(AppMailEvent.of(AppMailType.FinishRequestWithdraw, cio));
            return cio.getId();
        });
    }

}
