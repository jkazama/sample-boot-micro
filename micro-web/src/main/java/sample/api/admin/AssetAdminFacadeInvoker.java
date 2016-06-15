package sample.api.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import sample.api.RestInvokerSupport;
import sample.microasset.api.admin.AssetAdminFacade;
import sample.microasset.model.asset.CashInOut;
import sample.microasset.model.asset.CashInOut.FindCashInOut;

/**
 * 資産系社内ユースケースの API 実行処理を表現します。
 */
@Component
public class AssetAdminFacadeInvoker extends RestInvokerSupport implements AssetAdminFacade {

    @Value("${extension.remoting.asset}")
    String applicationName;
    
    /** {@inheritDoc} */
    @Override
    public String applicationName() {
        return applicationName;
    }
    
    /** {@inheritDoc} */
    @Override
    public String rootPath() {
        return Path;
    }
    
    /** {@inheritDoc} */
    @Override
    public List<CashInOut> findCashInOut(FindCashInOut p) {
        return invoker().get(PathFindCashInOut, new ParameterizedTypeReference<List<CashInOut>>() {}, p);
    }
    
    /** {@inheritDoc} */
    @Override
    public ResponseEntity<Void> closingCashOut() {
        return invoker().postEntity(PathClosingCashOut, Void.class, null);
    }
    
    /** {@inheritDoc} */
    @Override
    public ResponseEntity<Void> realizeCashflow() {
        return invoker().postEntity(PathRealizeCashflow, Void.class, null);
    }
    
}
