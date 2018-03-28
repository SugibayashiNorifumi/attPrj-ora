package application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.dao.MUserDao;
import application.dto.UserInfoDto;
import application.entity.MUser;

@Service
@Transactional
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    MUserDao muserDao;

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
     *
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

    /**
     * ユーザ情報を検索する。
     *
     * @param orgCd 所属組織コード
     * @param name 名前
     *
     * @return ユーザ情報リスト
     */
    public List<UserInfoDto> findUsers(String orgCd, String name) {
        return muserDao.findUsers(orgCd, name);
    }

    /**
     * ユーザ情報を登録する。
     *
     * @param user ユーザ情報
     */
    public void registerUser(MUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        muserDao.insert(user);
    }
}
