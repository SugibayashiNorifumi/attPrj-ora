package application.emuns;

/**
 * メニューコード
 *
 * @author 作成者氏名
 *
 */
public enum MenuCd {
    ARRIVAL("01"), // 出勤
    CLOCK_OUT("02"), // 退勤
    REWRITING("03"), // 修正
    LIST_OUTPUT("04"); // 退勤

    private String code;

    MenuCd(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}

