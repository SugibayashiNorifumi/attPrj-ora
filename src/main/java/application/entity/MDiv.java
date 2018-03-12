package application.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 汎用区分マスタエンティティ
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MDiv extends AbstractEntity {

    /** 汎用区分ID */
    public Integer divId;

    /** 区分名 */
    public String divName;

    /** 区分物理名 */
    public String divPhysicalName;

    /** 変更可能フラグ */
    public String allowUpdateFlg;

}
