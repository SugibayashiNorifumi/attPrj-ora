package application.entity;

import java.util.Date;

/**
 * 勤怠情報エンティティ
 */
public class TAttendance extends AbstractEntity {

    /** ユーザID */
    public Integer userId;

    /** 勤怠区分コード(汎用区分:2を参照) */
    public String attendanceCd;

    /** 出勤日(yyyymmdd形式) */
    public String attendanceDay;

    /** 勤怠時刻 */
    public Date attendanceTime;

    /** 修正フラグ */
    public String mail;

}
