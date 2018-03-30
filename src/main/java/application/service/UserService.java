package application.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.dao.MUserDao;
import application.entity.MUser;

/**
 * ユーザ情報操作サービスクラス。
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    MUserDao muserDao;

    /**
     * ユーザIDをもとにユーザを取得する。
     * @param userId ユーザID
     * @return ユーザ情報
     */
    public MUser getUserByUserId(Integer userId) {
        return muserDao.getByPk(userId);
    }

    /**
     * メールアドレスをもとにユーザを取得する。
     * @param mail メールアドレス
     * @return ユーザ情報
     */
    public Optional<MUser> getUserByMail(String mail) {
        return muserDao.selectByMail(mail);
    }

    /**
     * LINE IDをもとにユーザを取得する。
     * @param lineId LINE ID
     * @return ユーザ情報
     */
    public Optional<MUser> getUserByLineId(String lineId) {
        return muserDao.selectByLineId(lineId);
    }

    /**
     * LINE IDを登録する。
     * @param userId 対象ユーザID
     * @param lineId LINE ID
     */
    public void registerLineId(Integer userId, String lineId) {
        muserDao.selectByPk(userId).ifPresent(muser -> {
            MUser entity = new MUser();
            entity.setUserId(userId);
            entity.setLineId(lineId);
            muserDao.update(entity);
        });
    }
}
