package sample.api.admin;

import static sample.api.admin.MasterAdminFacade.Path;

import java.util.*;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sample.api.RestExporterSupport;
import sample.model.master.*;
import sample.model.master.Holiday.RegHoliday;
import sample.usecase.MasterAdminService;

/**
 * マスタ系社内ユースケースの外部公開処理を表現します。
 */
@RestController
@RequestMapping(Path)
public class MasterAdminFacadeExporter extends RestExporterSupport implements MasterAdminFacade {

    @Autowired
    MasterAdminService service;
    
    /** {@inheritDoc} */
    @Override
    @GetMapping(PathGetStaff)
    public Staff getStaff(@PathVariable String staffId) {
        return service.getStaff(staffId).orElse(null);
    }
    
    /** {@inheritDoc} */
    @Override
    @GetMapping(PathFindStaffAuthority)
    public List<StaffAuthority> findStaffAuthority(@PathVariable String staffId) {
        return service.findStaffAuthority(staffId);
    }
    
    /** {@inheritDoc} */
    @Override
    @PostMapping(PathRegisterHoliday)
    public ResponseEntity<Void> registerHoliday(@RequestBody @Valid RegHoliday p) {
        return resultEmpty(() -> service.registerHoliday(p));
    }
    
}
