package sample.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import sample.context.rest.RestInvoker;
import sample.microasset.api.AssetFacade;
import sample.microasset.model.asset.CashInOut;
import sample.microasset.model.asset.CashInOut.RegCashOut;

/**
 * 資産系ユースケースの API 実行処理を表現します。
 */
@Component
public class AssetFacadeInvoker implements AssetFacade {

    private final ApiClient client;
    private final String appliationName;
    
    public AssetFacadeInvoker(
            ApiClient client,
            @Value("${extension.remoting.asset}")
            String applicationName) {
        this.client = client;
        this.appliationName = applicationName;
    }
    
    /** {@inheritDoc} */
    @Override
    public List<CashInOut> findUnprocessedCashOut() {
        return invoker().get(PathFindUnprocessedCashOut, new ParameterizedTypeReference<List<CashInOut>>() {});
    }
    
    private RestInvoker invoker() {
        return client.invoker(appliationName, Path);
    }
    
    /** {@inheritDoc} */
    @Override
    public ResponseEntity<Long> withdraw(RegCashOut p) {
        return invoker().postEntity(PathWithdraw, Long.class, p);
    }
    
}
