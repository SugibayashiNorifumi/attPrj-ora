package application.service;

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
     * @param setting 設定マスタエンティティ
     */
    public void registerSetting(MSetting setting) {
        setting.setBusinessFlagMon(toFlag(setting.getBusinessFlagMon()));
        setting.setBusinessFlagTue(toFlag(setting.getBusinessFlagTue()));
        setting.setBusinessFlagWed(toFlag(setting.getBusinessFlagWed()));
        setting.setBusinessFlagThu(setting.getBusinessFlagThu());
        setting.setBusinessFlagFri(setting.getBusinessFlagFri());
        setting.setBusinessFlagSat(setting.getBusinessFlagSat());
        setting.setBusinessFlagSun(setting.getBusinessFlagSun());
        setting.setAlertFlag(setting.getAlertFlag());
        mSettingDao.update(setting);
    }

    /**
     * フラグ値に変換する。
     * @param str 変換元の文字列
     * @return 値が存在する場合"1" その他"0"
     */
    private String toFlag(String str) {
        if (StringUtils.isEmpty(str)) {
            return DelFlag.OFF.getVal();
        }
        return DelFlag.ON.getVal();
    }
}
