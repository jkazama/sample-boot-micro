package sample.microasset.api.admin;

import static sample.microasset.api.admin.AssetAdminFacade.Path;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sample.api.ApiUtils;
import sample.microasset.model.asset.CashInOut;
import sample.microasset.model.asset.CashInOut.FindCashInOut;
import sample.microasset.usecase.AssetAdminService;

/**
 * 資産系社内ユースケースの外部公開処理を表現します。
 */
@RestController
@RequestMapping(Path)
public class AssetAdminFacadeExporter implements AssetAdminFacade {

    private final AssetAdminService service;
    
    public AssetAdminFacadeExporter(AssetAdminService service) {
        this.service = service;
    }
    
    /** {@inheritDoc} */
    @Override
    @GetMapping(PathFindCashInOut)
    public List<CashInOut> findCashInOut(@Valid FindCashInOut p) {
        return service.findCashInOut(p);
    }
    
    /** {@inheritDoc} */
    @Override
    @PostMapping(PathClosingCashOut)
    public ResponseEntity<Void> closingCashOut() {
        return ApiUtils.resultEmpty(() -> service.closingCashOut());
    }
    
    /** {@inheritDoc} */
    @Override
    @PostMapping(PathRealizeCashflow)
    public ResponseEntity<Void> realizeCashflow() {
        return ApiUtils.resultEmpty(() -> service.realizeCashflow());
    }

}
