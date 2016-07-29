package sample.api.admin;

import java.util.*;

import org.springframework.http.ResponseEntity;

import sample.model.master.*;
import sample.model.master.Holiday.RegHoliday;

/**
 * マスタドメインに対する社内ユースケース API を表現します。
 */
public interface MasterAdminFacade {

    String Path = "/api/admin/master";
    
    String PathGetStaff = "/staff/{staffId}/";
    String PathFindStaffAuthority = "/staff/{staffId}/authority";
    String PathRegisterHoliday = "/holiday";
    
    /** 社員を取得します。 */
    Staff getStaff(String staffId);

    /** 社員権限を取得します。 */
    List<StaffAuthority> findStaffAuthority(String staffId);

    /** 休日を登録します。 */
    ResponseEntity<Void> registerHoliday(final RegHoliday p);

}
