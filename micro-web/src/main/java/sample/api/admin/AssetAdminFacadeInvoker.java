package sample.api.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import sample.api.RestInvokerSupport;
import sample.model.asset.CashInOut;
import sample.model.asset.CashInOut.FindCashInOut;

@Component
public class AssetAdminFacadeInvoker extends RestInvokerSupport implements AssetAdminFacade {

    @Value("${extension.remoting.target}")
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
