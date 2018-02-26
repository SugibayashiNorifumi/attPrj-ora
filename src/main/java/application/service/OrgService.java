package application.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.dao.MOrgDao;
import application.entity.MOrg;

/**
 * 組織サービス
 */
@Service
@Transactional
public class OrgService {

    @Autowired
    private MOrgDao mOrgDao;

    /**
     * 組織を検索する
     * @return 組織情報リスト
     */
    public List<MOrg> findOrgs() {
        return mOrgDao.findOrgs();
    }

}
