package sample.api;

import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import sample.model.asset.CashInOut;
import sample.model.asset.CashInOut.RegCashOut;

@Component
public class AssetFacadeInvoker extends RestInvokerSupport implements AssetFacade {

    @Value("${extension.remoting.target}")
    private String applicationName;
    
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
    public List<CashInOut> findUnprocessedCashOut() {
        return invoker().get(PathFindUnprocessedCashOut, new ParameterizedTypeReference<List<CashInOut>>() {});
    }
    
    /** {@inheritDoc} */
    @Override
    public ResponseEntity<Long> withdraw(RegCashOut p) {
        return invoker().postEntity(PathWithdraw, Long.class, p);
    }
    
}
