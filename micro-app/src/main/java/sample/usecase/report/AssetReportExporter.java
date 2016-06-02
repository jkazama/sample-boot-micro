package sample.usecase.report;

import java.io.InputStream;

import org.springframework.stereotype.Component;

import sample.model.asset.CashInOut.FindCashInOut;

/**
 * 資産ドメインの帳票出力コンポーネントを表現します。
 */
@Component
public class AssetReportExporter extends ServiceReportExporter {

    /**　振込入出金情報をCSV出力します。 */
    public byte[] exportCashInOut(final FindCashInOut p) {
        //low: バイナリ生成。条件指定を可能にしたオンラインダウンロードを想定。
        return new byte[0];
    }

    public void exportAnyBigData(final InputStream ins, final FindCashInOut p) {
        //low: サイズが多いケースではストリームへ都度書き出しする。
    }

    /**　振込入出金情報を帳票出力します。 */
    public void exportFileCashInOut(String baseDay) {
        //low: 特定のディレクトリへのファイル出力。ジョブ等での利用を想定
    }

}
