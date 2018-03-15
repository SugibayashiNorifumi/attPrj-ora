package application.emuns;

/**
 * 勤怠区分コード
 *
 * @author 作成者氏名
 *
 */
public enum AttenanceCd {
    MEMBER("01"), // 出勤
    MANAGER("02"); // 退勤

    private String code;

    AttenanceCd(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
