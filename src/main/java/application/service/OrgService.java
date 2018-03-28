package application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.dao.MOrgDao;
import application.entity.MOrg;
import application.security.AdminUser;

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
     * @param name 名前
     * @return 組織情報リスト*
     */
    public List<MOrg> findOrgs(String name) {
        return mOrgDao.findOrgs(name);
    }

    public void registerOrg(MOrg org) {
        AdminUser principal = (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        org.setRegistDate(LocalDateTime.now());
        org.setRegistUserId(principal.getUser().getUserId());
        org.setRegistFuncCd("0");
        mOrgDao.insert(org);
    }
}
