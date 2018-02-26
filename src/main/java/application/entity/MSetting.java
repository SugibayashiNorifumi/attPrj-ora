package application.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 設定マスタエンティティ
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class MSetting extends AbstractMasterEntity {
    public String openTime = "09";
    public String openMinutes = "00";
    public String closeTime = "20";
    public String closeMinutes = "00";
    public String alertOpenTime = "08";
    public String alertOpenMinutes = "55";
    public String alertCloseTime = "20";
    public String alertCloseMinutes = "00";
    public String businessFlagMon = "1";
    public String businessFlagTue = "1";
    public String businessFlagWed = "1";
    public String businessFlagThu = "1";
    public String businessFlagFri = "1";
    public String businessFlagSat = "0";
    public String businessFlagSun = "0";
    public String alertFlag = "1";
}
