package sample.api;

import static sample.api.AssetFacade.Path;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sample.model.asset.CashInOut;
import sample.model.asset.CashInOut.RegCashOut;
import sample.usecase.AssetService;

/**
 * 資産系ユースケースの外部公開処理を表現します。
 */
@RestController
@RequestMapping(Path)
public class AssetFacadeExporter extends RestExporterSupport implements AssetFacade {

    @Autowired
    AssetService service;
    
    /** {@inheritDoc} */
    @Override
    @RequestMapping(PathFindUnprocessedCashOut)
    public List<CashInOut> findUnprocessedCashOut() {
        return service.findUnprocessedCashOut();
    }

    /** {@inheritDoc} */
    @Override
    @RequestMapping(value = PathWithdraw, method = RequestMethod.POST)
    public ResponseEntity<Long> withdraw(@RequestBody @Valid RegCashOut p) {
        return result(() -> service.withdraw(p));
    }
    
}
