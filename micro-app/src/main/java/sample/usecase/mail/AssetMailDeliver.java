package sample.usecase.mail;

import org.springframework.stereotype.Component;

import sample.model.asset.CashInOut;

/**
 * 資産ドメインのメール送信コンポーネントを表現します。
 */
@Component
public class AssetMailDeliver extends ServiceMailDeliver {

    /** 出金依頼受付メールを送信します。 */
    public void sendWithdrawal(final CashInOut cio) {
        //low: サンプルなので未実装。実際は独自にトランザクションを貼って処理を行う
    }
    
}
