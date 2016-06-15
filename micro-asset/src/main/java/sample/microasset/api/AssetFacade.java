package sample.microasset.api;

import java.util.List;

import org.springframework.http.ResponseEntity;

import sample.microasset.model.asset.CashInOut;
import sample.microasset.model.asset.CashInOut.RegCashOut;

/**
 * 資産関連のユースケース API を表現します。
 */
public interface AssetFacade {
    
    String Path = "/api/asset";
    
    String PathFindUnprocessedCashOut = "/cio/unprocessedOut/";
    String PathWithdraw = "/cio/withdraw";
    
    /**
     * 未処理の振込依頼情報を検索します。
     * low: CashInOutは情報過多ですがアプリケーション層では公開対象を特定しにくい事もあり、
     * UI層に最終判断を委ねています。
     */
    List<CashInOut> findUnprocessedCashOut();
    
    /**
     * 振込出金依頼をします。
     * low: 公開リスクがあるためUI層には必要以上の情報を返さない事を意識します。
     * @return 振込出金依頼ID
     */
    ResponseEntity<Long> withdraw(RegCashOut p);
    
}
