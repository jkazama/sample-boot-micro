package sample.microasset.api;

import static sample.microasset.api.AssetFacade.Path;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sample.api.RestExporterSupport;
import sample.microasset.model.asset.CashInOut;
import sample.microasset.model.asset.CashInOut.RegCashOut;
import sample.microasset.usecase.AssetService;

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
    @GetMapping(PathFindUnprocessedCashOut)
    public List<CashInOut> findUnprocessedCashOut() {
        return service.findUnprocessedCashOut();
    }

    /** {@inheritDoc} */
    @Override
    @PostMapping(PathWithdraw)
    public ResponseEntity<Long> withdraw(@RequestBody @Valid RegCashOut p) {
        return result(() -> service.withdraw(p));
    }
    
}
