package sample.controller.admin;

import static sample.api.admin.MasterAdminFacade.*;

import java.util.*;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.*;
import sample.ValidationException;
import sample.ValidationException.ErrorKeys;
import sample.api.admin.MasterAdminFacade;
import sample.context.actor.Actor;
import sample.context.security.*;
import sample.context.security.SecurityActorFinder.ActorDetails;
import sample.model.master.Holiday.RegHoliday;

/**
 * マスタに関わる社内のUI要求を処理します。
 */
@RestController
@RequestMapping(Path)
public class MasterAdminController {

    private final SecurityProperties securityProps;
    private final MasterAdminFacade facade;
    
    public MasterAdminController(SecurityProperties securityProps, MasterAdminFacade facade) {
        this.securityProps = securityProps;
        this.facade = facade;
    }

    /** 社員ログイン状態を確認します。 */
    @RequestMapping("/loginStatus")
    public boolean loginStatus() {
        return true;
    }

    /** 社員ログイン情報を取得します。 */
    @GetMapping("/loginStaff")
    public LoginStaff loadLoginStaff() {
        if (securityProps.auth().isEnabled()) {
            ActorDetails actorDetails = SecurityActorFinder.actorDetails()
                    .orElseThrow(() -> new ValidationException(ErrorKeys.Authentication));
            Actor actor = actorDetails.actor();
            return new LoginStaff(actor.getId(), actor.getName(), actorDetails.getAuthorityIds());
        } else { // for dummy login
            return new LoginStaff("sample", "sample", new ArrayList<>());
        }
    }

    /** クライアント利用用途に絞ったパラメタ */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class LoginStaff {
        private String id;
        private String name;
        private Collection<String> authorities;
    }

    /** 休日を登録します。 */
    @PostMapping(PathRegisterHoliday)
    public ResponseEntity<Void> registerHoliday(@Valid RegHoliday p) {
        return facade.registerHoliday(p);
    }

}
