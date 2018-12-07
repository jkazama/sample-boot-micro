package sample.api;

import java.util.function.Supplier;

import org.springframework.http.*;

/** APIで利用されるユーティリティを表現します。 */
public abstract class ApiUtils {

    /** 戻り値を生成して返します。(戻り値がプリミティブまたはnullを許容する時はこちらを利用してください) */
    public static <T> ResponseEntity<T> result(Supplier<T> command) {
        return ResponseEntity.status(HttpStatus.OK).body(command.get());
    }

    /** 空の戻り値を生成して返します。 */
    public static ResponseEntity<Void> resultEmpty(Runnable command) {
        command.run();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    
}
