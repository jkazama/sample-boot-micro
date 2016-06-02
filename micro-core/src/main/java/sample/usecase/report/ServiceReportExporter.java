package sample.usecase.report;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.*;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.Setter;
import sample.context.orm.DefaultRepository;
import sample.context.report.ReportHandler;
import sample.usecase.ServiceUtils;

/**
 * アプリケーション層のレポート出力を行う基底コンポーネント。
 * <p>独自にトランザクションを管理するので、サービスのトランザクション内で
 * 呼び出さないように注意してください。
 */
@Setter
public class ServiceReportExporter {

    @Autowired
    protected DefaultRepository rep;
    @Autowired
    @Qualifier(DefaultRepository.BeanNameTx)
    private PlatformTransactionManager tx;
    @Autowired
    protected ReportHandler report;

    /** トランザクション処理を実行します。 */
    protected <T> T tx(Supplier<T> callable) {
        return ServiceUtils.tx(tx, callable);
    }

    /** トランザクション処理を実行します。 */
    protected void tx(Runnable command) {
        ServiceUtils.tx(tx, command);
    }

}
