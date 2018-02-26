package application.form;

import org.springframework.util.StringUtils;

import lombok.Data;

/**
 * 設定マスタ画面フォーム
 */
@Data
public class SettingForm {
    public String openTime;
    public String openMinutes;
    public String closeTime;
    public String closeMinutes;
    public String alertOpenTime;
    public String alertOpenMinutes;
    public String alertCloseTime;
    public String alertCloseMinutes;
    public String businessFlagMon;
    public String businessFlagTue;
    public String businessFlagWed;
    public String businessFlagThu;
    public String businessFlagFri;
    public String businessFlagSat;
    public String businessFlagSun;
    public String alertFlag;

    public void setStartTime(String value) {
        if(!StringUtils.isEmpty(value)) {
            String[] startTime = value.split(":");
            if(startTime.length == 2) {
                openTime = startTime[0];
                openMinutes = startTime[1];
            }
        }
    }

    public void setEndTime(String value) {
        if(!StringUtils.isEmpty(value)) {
            String[] endTime = value.split(":");
            if(endTime.length == 2) {
                closeTime = endTime[0];
                closeMinutes = endTime[1];
            }
        }
    }

    public void setAlertEndTime(String value) {
        if(!StringUtils.isEmpty(value)) {
            String[] alertEndTime = value.split(":");
            if(alertEndTime.length == 2) {
                alertCloseTime = alertEndTime[0];
                alertCloseMinutes = alertEndTime[1];
            }
        }
    }

    public String getStartTime() {
        return openTime + ":" + openMinutes;
    }

    public String getEndTime() {
        return closeTime + ":" + closeMinutes;
    }

    public String getAlertStartTime() {
        return alertOpenTime + ":" + alertOpenMinutes;
    }

    public String getAlertEndTime() {
        return alertCloseTime + ":" + alertCloseMinutes;
    }
}
