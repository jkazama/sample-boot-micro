package sample.api.admin;

import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import sample.api.RestInvokerSupport;
import sample.model.master.*;
import sample.model.master.Holiday.RegHoliday;

@Component
public class MasterAdminFacadeInvoker extends RestInvokerSupport implements MasterAdminFacade {

    @Value("${extension.remoting.target}")
    String applicationName;
    
    /** {@inheritDoc} */
    @Override
    public String applicationName() {
        return applicationName;
    }
    
    /** {@inheritDoc} */
    @Override
    public String rootPath() {
        return Path;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Staff> getStaff(String staffId) {
        return Optional.ofNullable(invoker().get(PathGetStaff, Staff.class, staffId));
    }
    
    /** {@inheritDoc} */
    @Override
    public List<StaffAuthority> findStaffAuthority(String staffId) {
        return invoker().get(PathFindStaffAuthority, new ParameterizedTypeReference<List<StaffAuthority>>() {}, staffId);
    }
    
    /** {@inheritDoc} */
    @Override
    public ResponseEntity<Void> registerHoliday(RegHoliday p) {
        return invoker().postEntity(PathRegisterHoliday, Void.class, p);
    }
    
}
