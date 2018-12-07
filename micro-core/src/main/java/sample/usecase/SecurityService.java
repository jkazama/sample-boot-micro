package sample.usecase;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import sample.ValidationException.ErrorKeys;
import sample.context.orm.DefaultRepository;
import sample.context.security.SecurityActorFinder.*;
import sample.context.security.SecurityConfigurer;
import sample.model.account.*;
import sample.model.master.*;
import sample.util.ConvertUtils;

/**
 * SpringSecurityのユーザアクセスコンポーネントを定義します。
 */
@Configuration
@ConditionalOnBean(SecurityConfigurer.class)
public class SecurityService {

    /** 一般利用者情報を提供します。(see SecurityActorFinder) */
    @Bean
    public SecurityUserService securityUserService(final SecurityDataHandler handler) {
        return new SecurityUserService() {
            /**
             * 以下の手順で利用口座を特定します。
             * <ul>
             * <li>ログインID(全角は半角に自動変換)に合致するログイン情報があるか
             * <li>口座IDに合致する有効な口座情報があるか
             * </ul>
             * <p>一般利用者には「ROLE_USER」の権限が自動で割り当てられます。
             */
            @Override
            public ActorDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return (ActorDetails) Optional.ofNullable(username).map(ConvertUtils::zenkakuToHan)
                        .flatMap((loginId) -> handler.getLoginByLoginId(loginId)
                                .flatMap((login) -> handler.getAccount(login.getId()).map((account) -> {
                    List<GrantedAuthority> authorities = Arrays.asList(new GrantedAuthority[] {
                            new SimpleGrantedAuthority("ROLE_USER") });
                    return new ActorDetails(account.actor(), login.getPassword(), authorities);
                }))).orElseThrow(() -> new UsernameNotFoundException(ErrorKeys.Login));
            }
        };
    }

    /** 社内管理向けの利用者情報を提供します。(see SecurityActorFinder) */
    @Bean
    @ConditionalOnProperty(name = "extension.security.auth.admin", havingValue = "true", matchIfMissing = false)
    public SecurityAdminService securityAdminService(final SecurityDataHandler handler) {
        return new SecurityAdminService() {
            /**
             * 以下の手順で社員を特定します。
             * <ul>
             * <li>社員ID(全角は半角に自動変換)に合致する社員情報があるか
             * <li>社員情報に紐付く権限があるか
             * </ul>
             * <p>社員には「ROLE_ADMIN」の権限が自動で割り当てられます。
             */
            @Override
            public ActorDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return (ActorDetails) Optional.ofNullable(username).map(ConvertUtils::zenkakuToHan)
                        .flatMap((staffId) -> handler.getStaff(staffId).map((staff) -> {
                    List<GrantedAuthority> authorities = new ArrayList<>(Arrays.asList(new GrantedAuthority[] {
                            new SimpleGrantedAuthority("ROLE_ADMIN") }));
                    handler.findStaffAuthority(staffId)
                            .forEach((auth) -> authorities.add(new SimpleGrantedAuthority(auth.getAuthority())));
                    return new ActorDetails(staff.actor(), staff.getPassword(), authorities);
                })).orElseThrow(() -> new UsernameNotFoundException(ErrorKeys.Login));
            }
        };
    }
    
    @Bean
    @ConditionalOnMissingBean
    public SecurityDataHandler securityDataHandler() {
        return new SecurityDataHandlerImpl();
    }
    
    public static interface SecurityDataHandler {
        /** ログイン情報を取得します。 */
        Optional<Login> getLoginByLoginId(String loginId);
        /** 有効な口座情報を取得します。 */
        Optional<Account> getAccount(String id);
        /** 社員を取得します。 */
        Optional<Staff> getStaff(String id);
        /** 社員権限を取得します。 */
        List<StaffAuthority> findStaffAuthority(String staffId);
    }
    public static class SecurityDataHandlerImpl implements SecurityDataHandler {
        @Autowired
        private DefaultRepository rep;
        /** {@inheritDoc} */
        @Transactional(DefaultRepository.BeanNameTx)
        @Cacheable("AccountService.getLoginByLoginId")
        public Optional<Login> getLoginByLoginId(String loginId) {
            return Login.getByLoginId(rep, loginId);
        }
        /** {@inheritDoc} */
        @Transactional(DefaultRepository.BeanNameTx)
        @Cacheable("AccountService.getAccount")
        public Optional<Account> getAccount(String id) {
            return Account.getValid(rep, id);
        }
        /** {@inheritDoc} */
        @Transactional(DefaultRepository.BeanNameTx)
        @Cacheable("MasterAdminService.getStaff")
        public Optional<Staff> getStaff(String id) {
            return Staff.get(rep, id);
        }
        /** {@inheritDoc} */
        @Transactional(DefaultRepository.BeanNameTx)
        @Cacheable("MasterAdminService.findStaffAuthority")
        public List<StaffAuthority> findStaffAuthority(String staffId) {
            return StaffAuthority.find(rep, staffId);
        }
    }
    
}
