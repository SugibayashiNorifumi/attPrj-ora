package application.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import application.dao.MSettingDao;
import application.emuns.DelFlag;
import application.entity.MSetting;

/**
 * 設定サービス
 *
 * @author 作成者名
 *
 */
@Service
@Transactional
public class SettingService {

    @Autowired
    private MSettingDao mSettingDao;

    /**
     * 設定マスタ情報を取得する
     *
     * @return 設定情報
     */
    public Optional<MSetting> getSetting() {
        return mSettingDao.select();
    }

    /**
     *
     * @param setting
     *            設定マスタエンティティ
     */
    public void registerSetting(MSetting setting) {

        setting.businessFlagMon = StringUtils.isEmpty(setting.businessFlagMon) ? DelFlag.OFF.getVal()
                : DelFlag.ON.getVal();
        setting.businessFlagTue = StringUtils.isEmpty(setting.businessFlagTue) ? DelFlag.OFF.getVal()
                : DelFlag.ON.getVal();
        setting.businessFlagWed = StringUtils.isEmpty(setting.businessFlagWed) ? DelFlag.OFF.getVal()
                : DelFlag.ON.getVal();
        setting.businessFlagThu = StringUtils.isEmpty(setting.businessFlagThu) ? DelFlag.OFF.getVal()
                : DelFlag.ON.getVal();
        setting.businessFlagFri = StringUtils.isEmpty(setting.businessFlagFri) ? DelFlag.OFF.getVal()
                : DelFlag.ON.getVal();
        setting.businessFlagSat = StringUtils.isEmpty(setting.businessFlagSat) ? DelFlag.OFF.getVal()
                : DelFlag.ON.getVal();
        setting.businessFlagSun = StringUtils.isEmpty(setting.businessFlagSun) ? DelFlag.OFF.getVal()
                : DelFlag.ON.getVal();
        setting.alertFlag = StringUtils.isEmpty(setting.alertFlag) ? DelFlag.OFF.getVal() : DelFlag.ON.getVal();

        setting.updateUserId = 0;
        setting.updateFuncCd = "0";
        setting.updateDate = LocalDateTime.now();

        mSettingDao.update(setting);
    }
}
