package application.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.dao.MUserDao;
import application.dto.UserInfo;
import application.emuns.AuthCd;
import application.emuns.Flag;
import application.entity.MUser;

@Service
@Transactional
public class UserService {

	private final static Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	MUserDao muserDao;

	public Optional<MUser> getUserByMail(String mail) {
		return muserDao.selectByMail(mail);
	}

	public void registerLineId(Integer userId, String lineId) {
	    muserDao.selectByPk(userId).ifPresent(muser -> {
	        MUser entity = new MUser();
	        entity.userId = userId;
	        entity.lineId = lineId;
	        muserDao.update(entity);
	    });
	}

	public Optional<MUser> adminLogin(String userId, String password) {
		try {
			new Long(userId);
		} catch (NumberFormatException ex) {
			return Optional.empty();
		}

		return muserDao.selectByPk(new Integer(userId))
				.filter(muser ->
						muser.delFlg.equals(Flag.OFF.getVal())
								&& muser.authCd.equals(AuthCd.ADMIN.getCode())
								&& passwordEncoder.matches(password, muser.password)
				);
	}

	public List<UserInfo> findUsers(String orgCd) {
		return muserDao.findUsers(orgCd);
	}

}
