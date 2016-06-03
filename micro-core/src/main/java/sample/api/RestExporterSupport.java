package sample.api;

import java.util.function.Supplier;

import org.springframework.http.*;

/**
 * Spring Cloud Netflix 標準の API サーバ実行アプローチをサポートします。
 * <p>API サーバ側の FacadeExporter ( RestContoroller ) 基底処理として利用してください。
 */
public abstract class RestExporterSupport {

    /** 戻り値を生成して返します。(戻り値がプリミティブまたはnullを許容する時はこちらを利用してください) */
    protected <T> ResponseEntity<T> result(Supplier<T> command) {
        return ResponseEntity.status(HttpStatus.OK).body(command.get());
    }

    /** 空の戻り値を生成して返します。 */
    protected ResponseEntity<Void> resultEmpty(Runnable command) {
        command.run();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
