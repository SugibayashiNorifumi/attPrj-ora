package application.emuns;

/**
 * 論理削除フラグ
 */
public enum Flag {
    OFF("0"),
    ON("1");

    private String val;

    Flag(String val) {
        this.val = val;
    }

    public String getVal() {
        return this.val;
    }

}
