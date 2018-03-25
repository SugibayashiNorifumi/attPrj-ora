package application.dao;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.entity.TAttendance;
import ninja.cero.sqltemplate.core.SqlTemplate;

/**
 * 勤怠情報DAO。
 * @author 作成者氏名
 */
@Component
public class TAttendanceDao extends AbstractDao<TAttendance> {
    /** このクラスのロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(TAttendanceDao.class);

    /** DB操作用。 */
    @Autowired
    private SqlTemplate sqlTemplate;

    /**
     * PKを条件に1行取得する。
     * @param userId ユーザID
     * @return 取得した1行を含むSELECT結果。
     */
    public Optional<TAttendance> selectByPk(Integer userId) {
        return Optional.ofNullable(
                sqlTemplate.forObject("sql/TAttendanceDao/selectByPk.sql", TAttendance.class, userId));
    }

    /**
     * 年月を条件に複数行取得する。
     * @param userId ユーザID
     * @return SELECT結果
     */
    public Optional<TAttendance> selectByMonth(Integer userId, String day) {
        return Optional.ofNullable(
                sqlTemplate.forObject("sql/TAttendanceDao/selectByDay.sql", TAttendance.class, userId, day));
    }

    /**
     * 1行挿入する。
     * @param entity 挿入する1行
     */
    public int insert(TAttendance entity) {
        return sqlTemplate.update("sql/TAttendanceDao/insert.sql", entity);
    }

    /**
     * 1行更新する。
     * @param entity 更新する1行
     */
    public int update(TAttendance entity) {
        return sqlTemplate.update("sql/TAttendanceDao/update.sql", entity);
    }
}
