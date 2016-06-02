package sample.api.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;

import sample.context.AppSetting;
import sample.context.AppSetting.FindAppSetting;
import sample.context.audit.*;
import sample.context.audit.AuditActor.FindAuditActor;
import sample.context.audit.AuditEvent.FindAuditEvent;
import sample.context.orm.PagingList;

/**
 * システムドメインに対する社内ユースケース API を表現します。
 */
public interface SystemAdminFacade {

    String Path = "/api/admin/system";
    
    String PathFindAudiActor = "/audit/actor/";
    String PathFindAudiEvent = "/audit/event/";
    String PathFindAppSetting = "/setting/";
    String PathChangeAppSetting = "/setting/{id}?value={value}";
    String PathProcessDay = "/processDay";
    
    /** 利用者監査ログを検索します。 */
    PagingList<AuditActor> findAuditActor(FindAuditActor p);

    /** イベント監査ログを検索します。 */
    PagingList<AuditEvent> findAuditEvent(FindAuditEvent p);

    /** アプリケーション設定一覧を検索します。 */
    List<AppSetting> findAppSetting(FindAppSetting p);

    /** アプリケーション設定情報を変更します。 */
    ResponseEntity<Void> changeAppSetting(String id, String value);

    /** 営業日を進めます。 */
    ResponseEntity<Void> processDay();
    
}
