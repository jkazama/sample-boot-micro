package sample.context.rest;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.filter.GenericFilterBean;

import sample.context.actor.ActorSession;
import sample.context.rest.RestActorSessionInterceptor.RestActorSessionConverter;

/**
 * プロセス間で ActorSession を引き継ぎさせる Filter。 (受付側)
 * <p>あらかじめ要求元プロセスに RestActorSessionInterceptor を適用しておく必要があります。
 * <p>非同期 Servlet を利用する場合は利用できません。( 別途同様の仕組みを作成してください )
 */
public class RestActorSessionBindFilter extends GenericFilterBean {
    
    private final ActorSession session;
    
    public RestActorSessionBindFilter(ActorSession session) {
        this.session = session;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            String actorStr = ((HttpServletRequest)request).getHeader(RestActorSessionInterceptor.AttrActorSession);
            Optional.ofNullable(actorStr).ifPresent((str) ->
                session.bind(RestActorSessionConverter.convert(str)));
            chain.doFilter(request, response);
        } finally {
            session.unbind();
        }
    }
    
}
