package sample.usecase;

import java.util.function.Supplier;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.Setter;
import sample.context.DomainHelper;
import sample.context.actor.Actor;
import sample.context.audit.AuditHandler;
import sample.context.lock.IdLockHandler;
import sample.context.lock.IdLockHandler.LockType;
import sample.context.orm.DefaultRepository;
import sample.model.BusinessDayHandler;

/**
 * ユースケースサービスの基底クラス。
 */
@Setter
public abstract class ServiceSupport {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageSource msg;

    /** ドメイン層向けヘルパークラス */
    @Autowired
    protected DomainHelper dh;
    /** 標準スキーマのRepository */
    @Autowired
    protected DefaultRepository rep;
   
    @Autowired
    @Qualifier(DefaultRepository.BeanNameTx)
    private PlatformTransactionManager tx;
    
    /** ID ロックユーティリティ */
    @Autowired
    protected IdLockHandler idLock;
    /** 監査ユーティリティ */
    @Autowired
    protected AuditHandler audit;
    
    @Autowired
    @Lazy
    private BusinessDayHandler businessDay;

    /** トランザクション処理を実行します。 */
    protected <T> T tx(Supplier<T> callable) {
        return ServiceUtils.tx(tx, callable);
    }

    /** トランザクション処理を実行します。 */
    protected void tx(Runnable command) {
        ServiceUtils.tx(tx, command);
    }

    /** 口座ロック付でトランザクション処理を実行します。 */
    protected <T> T tx(String accountId, LockType lockType, final Supplier<T> callable) {
        return idLock.call(accountId, lockType, () -> {
            return tx(callable);
        });
    }

    /** 口座ロック付でトランザクション処理を実行します。 */
    protected void tx(String accountId, LockType lockType, final Runnable callable) {
        idLock.call(accountId, lockType, () -> {
            tx(callable);
            return true;
        });
    }

    /** i18nメッセージ変換を行います。 */
    protected String msg(String message) {
        return msg.getMessage(message, null, message, actor().getLocale());
    }
    
    /** i18nメッセージ変換を行います。 */
    protected String msg(String message, Object... args) {
        return msg.getMessage(message, args, message, actor().getLocale());
    }

    /** 利用者を返します。 */
    protected Actor actor() {
        return dh.actor();
    }

    /** 営業日ユーティリティを返します。 */
    protected BusinessDayHandler businessDay() {
        return businessDay;
    }

}
