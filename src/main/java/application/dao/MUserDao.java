package application.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import application.dto.UserInfoDto;
import application.entity.MUser;
import ninja.cero.sqltemplate.core.SqlTemplate;

/**
 * ユーザマスタDAO。
 * @author 作成者氏名
 */
@Component
public class MUserDao extends AbstractDao<MUser> {

    private static final Logger logger = LoggerFactory.getLogger(MUserDao.class);

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

    /**
     * 管理者のメンバーを取得する。
     * @param lineId LINE識別子
     * @param yyyymm 勤怠年月
     * @return メンバーリスト(リストの最後がlineIdが示すユーザ)
     */
    public List<MUser> findAdminMembers(String lineId, String yyyymm) {
        MUser myUser = getByLineId(lineId);
        Map<String, Object> cond = new HashMap<>();
        cond.put("lineId", lineId);
        cond.put("yyyymm", yyyymm);
        List<MUser> res = sqlTemplate.forList("sql/MUserDao/selectAdminMembers.sql", MUser.class, cond);
        res.add(myUser);
        return res;
    }

    /**
     * 上長のメンバーを取得する。
     * @param lineId LINE識別子
     * @param yyyymm 勤怠年月
     * @return メンバーリスト(リストの最後がlineIdが示すユーザ)
     */
    public List<MUser> findManagerMembers(String lineId, String yyyymm) {
        MUser myUser = getByLineId(lineId);
        Map<String, Object> cond = new HashMap<>();
        cond.put("lineId", lineId);
        cond.put("yyyymm", yyyymm);
        cond.put("managerId", myUser.getUserId());
        List<MUser> res = sqlTemplate.forList("sql/MUserDao/selectManagerMembers.sql", MUser.class, cond);
        res.add(myUser);
        return res;
    }

    public List<UserInfoDto> findUsers(String orgCd, String name) {
        Map<String, Object> cond = new HashMap<>();
        cond.put("orgCd", orgCd);
        if (!StringUtils.isEmpty(name)) {
            cond.put("likeName", "%" + name + "%");
        }
        return sqlTemplate.forList("sql/MUserDao/findUsers.sql", UserInfoDto.class, cond);
    }

    public int insert(MUser entity) {
        setInsertColumns(entity);
        return sqlTemplate.update("sql/MUserDao/insert.sql", entity);
    }

    public int update(MUser entity) {
        setUpdateColumns(entity);
        return sqlTemplate.update("sql/MUserDao/update.sql", entity);
    }

    /**
     * 勤怠未登録のユーザを取得する。
     * @param yyyyMMdd 出勤日
     * @param attendanceCd 登録確認対象とする勤怠区分コード
     * @return 勤怠未登録のユーザ
     */
    public List<MUser> findNoneAttendance(String yyyyMMdd, String attendanceCd) {
        Map<String, Object> cond = new HashMap<>();
        cond.put("attendanceDay", yyyyMMdd);
        cond.put("attendanceCd", attendanceCd);
        return sqlTemplate.forList("sql/MUserDao/findNoneAttendance.sql", MUser.class, cond);
    }
}
