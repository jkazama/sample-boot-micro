package sample.context.rest;

import java.io.IOException;
import java.util.*;

import org.slf4j.*;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;

import sample.context.actor.*;
import sample.context.actor.Actor.ActorRoleType;

/**
 * HTTP リクエスト時にヘッダへ ActorSession 情報を紐付けする Interceptor。
 */
public class RestActorSessionInterceptor implements ClientHttpRequestInterceptor {
    
    public static final String AttrActorSession = "ActorSession";
    
    private final ActorSession session;
    
    public RestActorSessionInterceptor(ActorSession session) {
        this.session = session;
    }
    
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        request.getHeaders().add(AttrActorSession, RestActorSessionConverter.convert(session.actor()));
        return execution.execute(request, body);
    }

    /** HTTP ヘッダへ ActorSession を紐付けする際の変換条件を表現します。 */
    public static class RestActorSessionConverter {
        private static final Logger logger = LoggerFactory.getLogger(RestActorSessionConverter.class);
        public static String convert(Actor actor) {
            return String.join("_", actor.getId(), actor.getName(), actor.getRoleType().name(),
                    actor.getLocale().getLanguage(),
                    Optional.ofNullable(actor.getChannel()).orElse("none"),
                    Optional.ofNullable(actor.getSource()).orElse("none"));
        }
        public static Actor convert(String actorStr) {
            String[] params = actorStr.split("_");
            if (params.length != 6) {
                logger.warn("Actor への変換に失敗しました。");
                return Actor.Anonymous;
            }
            return new Actor(params[0], params[1], ActorRoleType.valueOf(params[2]),
                    new Locale(params[3]), params[4], params[5]);
        }
    }
    
}
