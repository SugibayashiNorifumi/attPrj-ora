package application.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import application.entity.MUser;

/**
 * ログイン管理ユーザ
 */
public class AdminUser extends User {
    private MUser mUser;

    public AdminUser(MUser mUser) {
        super(mUser.getUserId().toString(), mUser.getPassword(), AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
        this.mUser = mUser;
    }

    public MUser getUser() {
        return this.mUser;
    }
}
