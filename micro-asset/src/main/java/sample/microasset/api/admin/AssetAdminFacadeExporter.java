package sample.microasset.api.admin;

import static sample.microasset.api.admin.AssetAdminFacade.Path;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sample.api.RestExporterSupport;
import sample.microasset.model.asset.CashInOut;
import sample.microasset.model.asset.CashInOut.FindCashInOut;
import sample.microasset.usecase.AssetAdminService;

/**
 * 資産系社内ユースケースの外部公開処理を表現します。
 */
@RestController
@RequestMapping(Path)
public class AssetAdminFacadeExporter extends RestExporterSupport implements AssetAdminFacade {

    @Autowired
    AssetAdminService service;
    
    /** {@inheritDoc} */
    @Override
    @RequestMapping(PathFindCashInOut)
    public List<CashInOut> findCashInOut(@Valid FindCashInOut p) {
        return service.findCashInOut(p);
    }
    
    /** {@inheritDoc} */
    @Override
    @RequestMapping(value = PathClosingCashOut, method = RequestMethod.POST)
    public ResponseEntity<Void> closingCashOut() {
        return resultEmpty(() -> service.closingCashOut());
    }
    
    /** {@inheritDoc} */
    @Override
    @RequestMapping(value = PathRealizeCashflow, method = RequestMethod.POST)
    public ResponseEntity<Void> realizeCashflow() {
        return resultEmpty(() -> service.realizeCashflow());
    }

}
