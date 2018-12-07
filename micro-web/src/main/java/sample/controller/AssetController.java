package sample.controller;

import static sample.microasset.api.AssetFacade.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.Value;
import sample.ActionStatusType;
import sample.context.Dto;
import sample.microasset.api.AssetFacade;
import sample.microasset.model.asset.CashInOut;
import sample.microasset.model.asset.CashInOut.RegCashOut;

/**
 * 資産に関わる顧客のUI要求を処理します。
 */
@RestController
@RequestMapping(Path)
public class AssetController {
    
    private final AssetFacade facade;
    
    public AssetController(AssetFacade facade) {
        this.facade = facade;
    }
    
    /** 未処理の振込依頼情報を検索します。 */
    @GetMapping(PathFindUnprocessedCashOut)
    public List<CashOutUI> findUnprocessedCashOut() {
        return facade.findUnprocessedCashOut().stream()
                .map((cio) -> CashOutUI.of(cio))
                .collect(Collectors.toList());
    }

    /** 振込出金依頼をします。  */
    @PostMapping(PathWithdraw)
    public ResponseEntity<Long> withdraw(@Valid RegCashOut p) {
        return facade.withdraw(p);
    }

    /** 振込出金依頼情報の表示用Dto */
    @Value
    static class CashOutUI implements Dto {
        private static final long serialVersionUID = 1L;
        private Long id;
        private String currency;
        private BigDecimal absAmount;
        private LocalDate requestDay;
        private LocalDateTime requestDate;
        private LocalDate eventDay;
        private LocalDate valueDay;
        private ActionStatusType statusType;
        private LocalDateTime updateDate;
        private Long cashflowId;

        public static CashOutUI of(final CashInOut cio) {
            return new CashOutUI(cio.getId(), cio.getCurrency(), cio.getAbsAmount(), cio.getRequestDay(),
                    cio.getRequestDate(), cio.getEventDay(), cio.getValueDay(), cio.getStatusType(),
                    cio.getUpdateDate(), cio.getCashflowId());
        }
    }

}
