package sample.controller.system;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sample.api.admin.SystemAdminFacade;
import sample.microasset.api.admin.AssetAdminFacade;

/**
 * システムジョブのUI要求を処理します。
 * low: 通常はバッチプロセス(または社内プロセスに内包)を別途作成して、ジョブスケジューラから実行される方式になります。
 * ジョブの負荷がオンライン側へ影響を与えないよう事前段階の設計が重要になります。
 * low: 社内/バッチプロセス切り出す場合はVM分散時の情報/排他同期を意識する必要があります。(DB同期/メッセージング同期/分散製品の利用 等)
 */
@RestController
@RequestMapping("/api/system/job")
public class JobController {

    private final AssetAdminFacade asset;
    private final SystemAdminFacade system;

    public JobController(AssetAdminFacade asset, SystemAdminFacade system) {
        this.asset = asset;
        this.system = system;
    }
    
    /** 営業日を進めます。 */
    @PostMapping("/daily/processDay")
    public ResponseEntity<Void> processDay() {
        return system.processDay();
    }

    /** 振込出金依頼を締めます。 */
    @PostMapping("/daily/closingCashOut")
    public ResponseEntity<Void> closingCashOut() {
        return asset.closingCashOut();
    }

    /** キャッシュフローを実現します。 */
    @PostMapping("/daily/realizeCashflow")
    public ResponseEntity<Void> realizeCashflow() {
        return asset.realizeCashflow();
    }

}
