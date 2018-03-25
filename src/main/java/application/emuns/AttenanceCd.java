package application.emuns;

/**
 * 勤怠区分コード
 *
 * @author 作成者氏名
 *
 */
public enum AttenanceCd {
    ARRIVAL("01"), // 出勤
    CLOCK_OUT("02"); // 退勤

    private String code;

    AttenanceCd(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
