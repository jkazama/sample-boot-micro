package sample.api.admin;

import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import sample.api.*;
import sample.context.rest.RestInvoker;
import sample.model.master.*;
import sample.model.master.Holiday.RegHoliday;

/**
 * マスタ系社内ユースケースの API 実行処理を表現します。
 */
@Component
public class MasterAdminFacadeInvoker implements MasterAdminFacade {

    private final ApiClient client;
    private final String appliationName;

    public MasterAdminFacadeInvoker(
            ApiClient client,
            @Value("${extension.remoting.app}") String applicationName) {
        this.client = client;
        this.appliationName = applicationName;
    }

    /** {@inheritDoc} */
    @Override
    public Staff getStaff(String staffId) {
        return invoker().get(PathGetStaff, Staff.class, staffId);
    }
    
    private RestInvoker invoker() {
        return client.invoker(appliationName, Path);
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
