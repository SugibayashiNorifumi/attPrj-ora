package application.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import application.dao.MSettingDao;
import application.emuns.Flag;
import application.entity.MSetting;

/**
 * 設定サービス
 */
@Service
@Transactional
public class SettingService {

    @Autowired
    private MSettingDao mSettingDao;


    /**
     * 設定情報を取得する
     *
     * @return 設定情報
     */
    public Optional<MSetting> getSetting() {
        return mSettingDao.select();
    }

    public void registerSetting(MSetting setting) {

        setting.businessFlagMon = StringUtils.isEmpty(setting.businessFlagMon) ? Flag.OFF.getVal() : Flag.ON.getVal();
        setting.businessFlagTue = StringUtils.isEmpty(setting.businessFlagTue) ? Flag.OFF.getVal() : Flag.ON.getVal();
        setting.businessFlagWed = StringUtils.isEmpty(setting.businessFlagWed) ? Flag.OFF.getVal() : Flag.ON.getVal();
        setting.businessFlagThu = StringUtils.isEmpty(setting.businessFlagThu) ? Flag.OFF.getVal() : Flag.ON.getVal();
        setting.businessFlagFri = StringUtils.isEmpty(setting.businessFlagFri) ? Flag.OFF.getVal() : Flag.ON.getVal();
        setting.businessFlagSat = StringUtils.isEmpty(setting.businessFlagSat) ? Flag.OFF.getVal() : Flag.ON.getVal();
        setting.businessFlagSun = StringUtils.isEmpty(setting.businessFlagSun) ? Flag.OFF.getVal() : Flag.ON.getVal();
        setting.alertFlag = StringUtils.isEmpty(setting.alertFlag) ? Flag.OFF.getVal() : Flag.ON.getVal();

        setting.updateUserId = 0;
        setting.updateFuncCd = "0";
        setting.updateDate = LocalDateTime.now();

        mSettingDao.update(setting);
    }
}
