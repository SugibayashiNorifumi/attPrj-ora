package application.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import application.dao.MUserDao;
import application.entity.MUser;

@Service
public class AdminUserDetailService implements UserDetailsService {

    @Autowired
    MUserDao mUserDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Integer userId;

        try {
            userId = new Integer(username);
        } catch (NumberFormatException ex) {
            throw new UsernameNotFoundException("user not found");
        }

        MUser mUser = mUserDao.selectByPk(userId).orElseThrow(() -> new UsernameNotFoundException("user not found"));

        return new AdminUser(mUser);
    }
}
