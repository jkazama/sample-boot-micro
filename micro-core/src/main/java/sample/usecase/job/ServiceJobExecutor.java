package sample.usecase.job;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.*;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.Setter;
import sample.context.orm.DefaultRepository;
import sample.usecase.ServiceUtils;

/**
 * アプリケーション層のジョブ実行を行う際の基底クラス。
 * <p>独自にトランザクションを管理するので、サービスのトランザクション内で 呼び出さないように注意してください。
 */
@Setter
public abstract class ServiceJobExecutor {

    @Autowired
    protected DefaultRepository rep;
    @Autowired
    @Qualifier(DefaultRepository.BeanNameTx)
    private PlatformTransactionManager tx;

    /** トランザクション処理を実行します。 */
    protected <T> T tx(Supplier<T> callable) {
        return ServiceUtils.tx(tx, callable);
    }

    /** トランザクション処理を実行します。 */
    protected void tx(Runnable command) {
        ServiceUtils.tx(tx, command);
    }
    
}
