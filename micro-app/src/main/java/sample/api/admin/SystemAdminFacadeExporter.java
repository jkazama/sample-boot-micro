package sample.api.admin;

import static sample.api.admin.SystemAdminFacade.*;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sample.api.RestExporterSupport;
import sample.context.AppSetting;
import sample.context.AppSetting.FindAppSetting;
import sample.context.audit.*;
import sample.context.audit.AuditActor.FindAuditActor;
import sample.context.audit.AuditEvent.FindAuditEvent;
import sample.context.orm.PagingList;
import sample.usecase.SystemAdminService;

/**
 * システム系社内ユースケースの外部公開処理を表現します。
 */
@RestController
@RequestMapping(Path)
public class SystemAdminFacadeExporter extends RestExporterSupport implements SystemAdminFacade {

    @Autowired
    SystemAdminService service;
    
    /** {@inheritDoc} */
    @Override
    @GetMapping(PathFindAudiActor)
    public PagingList<AuditActor> findAuditActor(@Valid FindAuditActor p) {
        return service.findAuditActor(p);
    }
    
    /** {@inheritDoc} */
    @Override
    @GetMapping(PathFindAudiEvent)
    public PagingList<AuditEvent> findAuditEvent(@Valid FindAuditEvent p) {
        return service.findAuditEvent(p);
    }
    
    /** {@inheritDoc} */
    @Override
    @GetMapping(PathFindAppSetting)
    public List<AppSetting> findAppSetting(@Valid FindAppSetting p) {
        return service.findAppSetting(p);
    }
    
    /** {@inheritDoc} */
    @Override
    @PostMapping(PathChangeAppSetting)
    public ResponseEntity<Void> changeAppSetting(String id, String value) {
        return resultEmpty(() -> service.changeAppSetting(id, value));
    }
    
    /** {@inheritDoc} */
    @Override
    @PostMapping(PathProcessDay)
    public ResponseEntity<Void> processDay() {
        return resultEmpty(() -> service.processDay());
    }
    
}
