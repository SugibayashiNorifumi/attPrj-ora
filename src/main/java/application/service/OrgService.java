package application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.context.AdminUser;
import application.dao.MOrgDao;
import application.entity.MOrg;

/**
 * 組織サービス。
 */
@Service
@Transactional
public class OrgService {

    /** 組織マスタDAO。 */
    @Autowired
    private MOrgDao mOrgDao;

    /**
     * 組織を検索する
     * @param name 名前
     * @return 組織情報リスト
     */
    public List<MOrg> findOrgs(String name) {
        return mOrgDao.findOrgs(name);
    }

    /**
     * 組織登録する。
     * @param org 組織
     */
    public void registerOrg(MOrg org) {
        AdminUser principal = (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        org.setRegistDate(LocalDateTime.now());
        org.setRegistUserId(principal.getUser().getUserId());
        org.setRegistFuncCd("0");
        mOrgDao.insert(org);
    }
}
