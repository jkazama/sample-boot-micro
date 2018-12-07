package sample.api.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import sample.api.*;
import sample.context.rest.RestInvoker;
import sample.microasset.api.admin.AssetAdminFacade;
import sample.microasset.model.asset.CashInOut;
import sample.microasset.model.asset.CashInOut.FindCashInOut;

/**
 * 資産系社内ユースケースの API 実行処理を表現します。
 */
@Component
public class AssetAdminFacadeInvoker implements AssetAdminFacade {

    private final ApiClient client;
    private final String appliationName;

    public AssetAdminFacadeInvoker(
            ApiClient client,
            @Value("${extension.remoting.asset}") String applicationName) {
        this.client = client;
        this.appliationName = applicationName;
    }
    
    /** {@inheritDoc} */
    @Override
    public List<CashInOut> findCashInOut(FindCashInOut p) {
        return invoker().get(PathFindCashInOut, new ParameterizedTypeReference<List<CashInOut>>() {}, p);
    }
    
    private RestInvoker invoker() {
        return client.invoker(appliationName, Path);
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
