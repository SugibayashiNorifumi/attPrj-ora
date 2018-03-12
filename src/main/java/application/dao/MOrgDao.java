package application.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.entity.MOrg;
import ninja.cero.sqltemplate.core.SqlTemplate;

/**
 * 組織マスタDAO
 *
 * @author 作成者氏名
 *
 */
@Component
public class MOrgDao {

    @Autowired
    private SqlTemplate sqlTemplate;

    /**
     * 指定条件で組織名を検索する。
     *
     * @return エンティティリスト
     */
    public List<MOrg> findOrgs() {
        return sqlTemplate.forList("sql/MOrgDao/findOrgs.sql", MOrg.class);
    }

    public int insert(MOrg entity) {
        return sqlTemplate.update("sql/MOrgDao/insert.sql", entity);
    }
}
