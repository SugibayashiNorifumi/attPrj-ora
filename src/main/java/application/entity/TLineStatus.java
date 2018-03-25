package application.entity;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LINEステータス情報エンティティ
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TLineStatus extends AbstractEntity {

    /** LINE識別子 */
    public String lineId;

    /** ユーザID */
    public Integer userId;

    /** メニューコード */
    public String menuCd;

    /** アクション名 */
    public String actionName;

    /** コンテンツ */
    public String contents;

    /** リクエスト時刻 */
    public Date requestTime;
}
