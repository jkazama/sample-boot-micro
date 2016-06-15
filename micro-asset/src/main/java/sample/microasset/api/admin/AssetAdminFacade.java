package sample.microasset.api.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;

import sample.microasset.model.asset.CashInOut;
import sample.microasset.model.asset.CashInOut.FindCashInOut;

/**
 * 資産ドメインに対する社内ユースケース API を表現します。
 */
public interface AssetAdminFacade {
    
    String Path = "/api/admin/asset";
    
    String PathFindCashInOut = "/cio/";
    String PathClosingCashOut = "/cio/closingCashOut";
    String PathRealizeCashflow = "/cf/realizeCashflow";
    
    /** 未処理の振込依頼情報を検索します。 */
    List<CashInOut> findCashInOut(FindCashInOut p);
 
    /**
     * 振込出金依頼を締めます。
     */
    ResponseEntity<Void> closingCashOut();

    /**
     * キャッシュフローを実現します。
     * <p>受渡日を迎えたキャッシュフローを残高に反映します。
     */
    ResponseEntity<Void> realizeCashflow();

}
