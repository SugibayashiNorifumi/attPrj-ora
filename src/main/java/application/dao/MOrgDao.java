package application.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import application.entity.MOrg;
import ninja.cero.sqltemplate.core.SqlTemplate;

/**
 * 組織マスタDAO
 *
 * @author 作成者氏名
 *
 */
@Component
public class MOrgDao extends AbstractDao<MOrg> {

    @Autowired
    private SqlTemplate sqlTemplate;

    /**
     * 指定条件で組織名を検索する。
     *
     * @return エンティティリスト
     */
    public List<MOrg> findOrgs(String name) {

        Map<String, Object> cond = new HashMap<>();

        if(!StringUtils.isEmpty(name)) {
            cond.put("likeName", "%" + name + "%");
        }

        return sqlTemplate.forList("sql/MOrgDao/findOrgs.sql", MOrg.class, cond);
    }

    public int insert(MOrg entity) {
        return sqlTemplate.update("sql/MOrgDao/insert.sql", entity);
    }

    public int update(MOrg entity) {
        return sqlTemplate.update("sql/MOrgDao/update.sql", entity);
    }
}
