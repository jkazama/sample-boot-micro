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
    @RequestMapping(PathGetStaff)
    public Optional<Staff> getStaff(String staffId) {
        return service.getStaff(staffId);
    }
    
    /** {@inheritDoc} */
    @Override
    @RequestMapping(PathFindStaffAuthority)
    public List<StaffAuthority> findStaffAuthority(String staffId) {
        return service.findStaffAuthority(staffId);
    }
    
    /** {@inheritDoc} */
    @Override
    @RequestMapping(value = PathRegisterHoliday, method = RequestMethod.POST)
    public ResponseEntity<Void> registerHoliday(@RequestBody @Valid RegHoliday p) {
        return resultEmpty(() -> service.registerHoliday(p));
    }
    
}
