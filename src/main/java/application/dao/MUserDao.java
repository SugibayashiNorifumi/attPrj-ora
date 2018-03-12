package application.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.dto.UserInfo;
import application.entity.MUser;
import ninja.cero.sqltemplate.core.SqlTemplate;

/**
 * ユーザマスタDAO
 *
 * @author 作成者氏名
 *
 */
@Component
public class MUserDao {

    @Autowired
    private SqlTemplate sqlTemplate;

    public Optional<MUser> selectByPk(Integer userId) {
        return Optional.ofNullable(sqlTemplate.forObject("sql/MUserDao/selectByPk.sql", MUser.class, userId));
    }

    public Optional<MUser> selectByMail(String mail) {
        return Optional.ofNullable(sqlTemplate.forObject("sql/MUserDao/selectByMail.sql", MUser.class, mail));
    }

    public List<UserInfo> findUsers(String orgCd) {
        return sqlTemplate.forList("sql/MUserDao/findUsers.sql", UserInfo.class, orgCd);
    }

    public int insert(MUser entity) {
        return sqlTemplate.update("sql/MUserDao/insert.sql", entity);
    }

    public int update(MUser entity) {
        entity.updateDate = LocalDateTime.now();
        return sqlTemplate.update("sql/MUserDao/update.sql", entity);
    }
}
