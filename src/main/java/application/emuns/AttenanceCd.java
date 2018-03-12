package application.emuns;

/**
 * 勤怠区分コード
 *
 * @author 作成者氏名
 *
 */
public enum AttenanceCd {
    MEMBER("01"), // 一般
    MANAGER("02"), // 上長
    ADMIN("03"); // 管理者

    private String code;

    AttenanceCd(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
