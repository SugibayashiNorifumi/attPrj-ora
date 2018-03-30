package application.dao;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.entity.MUser;
import ninja.cero.sqltemplate.core.SqlTemplate;

/**
 * ユーザマスタDAO。
 * @author 作成者氏名
 */
@Component
public class MUserDao extends AbstractDao<MUser> {
    //    /** このクラスのロガー。 */
    //    private static final Logger logger = LoggerFactory.getLogger(MUserDao.class);

    @Autowired
    private SqlTemplate sqlTemplate;

    public Optional<MUser> selectByPk(Integer userId) {
        return Optional.ofNullable(sqlTemplate.forObject("sql/MUserDao/selectByPk.sql", MUser.class, userId));
    }

    /**
     * PKでユーザを取得する。
     * @param userId ユーザID
     * @return ユーザ
     */
    public MUser getByPk(Integer userId) {
        Optional<MUser> select = selectByPk(userId);
        MUser res = null;
        if (select.isPresent()) {
            res = select.get();
        }
        return res;
    }

    public Optional<MUser> selectByMail(String mail) {
        return Optional.ofNullable(sqlTemplate.forObject("sql/MUserDao/selectByMail.sql", MUser.class, mail));
    }

    public Optional<MUser> selectByLineId(String lineId) {
        return Optional.ofNullable(sqlTemplate.forObject("sql/MUserDao/selectByLineId.sql", MUser.class, lineId));
    }

    /**
     * ユーザを取得する。
     * @param lineId LINE識別子
     * @return ユーザ
     */
    public MUser getByLineId(String lineId) {
        Optional<MUser> select = selectByLineId(lineId);
        MUser res = null;
        if (select.isPresent()) {
            res = select.get();
        }
        return res;
    }

    public int insert(MUser entity) {
        setInsertColumns(entity);
        return sqlTemplate.update("sql/MUserDao/insert.sql", entity);
    }

    public int update(MUser entity) {
        setUpdateColumns(entity);
        return sqlTemplate.update("sql/MUserDao/update.sql", entity);
    }
}
