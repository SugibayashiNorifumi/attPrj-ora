package application.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 設定マスタエンティティ
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MSetting extends AbstractEntity {

    /** 始業時刻（時） */
    public String openTime = "09";

    /** 始業時刻（分） */
    public String openMinutes = "00";

    /** 終業時刻（時） */
    public String closeTime = "20";

    /** 終業時刻（分） */
    public String closeMinutes = "00";

    /** アラート出勤時刻（時） */
    public String alertOpenTime = "08";

    /** アラート出勤時刻（分） */
    public String alertOpenMinutes = "55";

    /** アラート退勤時刻（時） */
    public String alertCloseTime = "20";

    /** アラート退勤時刻（分） */
    public String alertCloseMinutes = "00";

    /** 営業日フラグ(月) */
    public String businessFlagMon = "1";

    /** 営業日フラグ(火) */
    public String businessFlagTue = "1";

    /** 営業日フラグ(水) */
    public String businessFlagWed = "1";

    /** 営業日フラグ(木) */
    public String businessFlagThu = "1";

    /** 営業日フラグ(金) */
    public String businessFlagFri = "1";

    /** 営業日フラグ(土) */
    public String businessFlagSat = "0";

    /** 営業日フラグ(日) */
    public String businessFlagSun = "0";

    /** 打刻漏れ防止アラートフラグ */
    public String alertFlag = "1";
}
