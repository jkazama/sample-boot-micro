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
 * 社内システム系ユースケースの外部公開処理を表現します。
 */
@RestController
@RequestMapping(Path)
public class SystemAdminFacadeExporter extends RestExporterSupport implements SystemAdminFacade {

    @Autowired
    SystemAdminService service;
    
    /** {@inheritDoc} */
    @Override
    @RequestMapping(PathFindAudiActor)
    public PagingList<AuditActor> findAuditActor(@Valid FindAuditActor p) {
        return service.findAuditActor(p);
    }
    
    /** {@inheritDoc} */
    @Override
    @RequestMapping(PathFindAudiEvent)
    public PagingList<AuditEvent> findAuditEvent(@Valid FindAuditEvent p) {
        return service.findAuditEvent(p);
    }
    
    /** {@inheritDoc} */
    @Override
    @RequestMapping(PathFindAppSetting)
    public List<AppSetting> findAppSetting(@Valid FindAppSetting p) {
        return service.findAppSetting(p);
    }
    
    /** {@inheritDoc} */
    @Override
    @RequestMapping(value = PathChangeAppSetting, method = RequestMethod.POST)
    public ResponseEntity<Void> changeAppSetting(String id, String value) {
        return resultEmpty(() -> service.changeAppSetting(id, value));
    }
    
    /** {@inheritDoc} */
    @Override
    @RequestMapping(value = PathProcessDay, method = RequestMethod.POST)
    public ResponseEntity<Void> processDay() {
        return resultEmpty(() -> service.processDay());
    }
    
}
