package application.entity;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 勤怠情報エンティティ
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TAttendance extends AbstractEntity {
    /** ユーザID */
    public Integer userId;

    /** 出勤日(yyyymmdd) */
    public String attendanceDay;

    /** 勤怠時刻 */
    public Date attendanceTime;

    /** 勤怠区分コード(汎用区分:2を参照) */
    public String attendanceCd;

    /** 修正フラグ */
    public String editFlg;
}
