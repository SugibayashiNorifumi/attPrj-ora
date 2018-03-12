package application.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 汎用区分明細マスタエンティティ
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MDivDetail extends AbstractEntity {

    /** 汎用区分ID */
    public Integer divId;

    /** 汎用区分コード */
    public String divCd;

    /** 汎用区分コード内容 */
    public String divCdContent;

    /** フリー属性１ */
    public String freeAttribute01;

    /** フリー属性２ */
    public String freeAttribute02;

    /** フリー属性３ */
    public String freeAttribute03;

    /** フリー属性４ */
    public String freeAttribute04;

    /** フリー属性５ */
    public String freeAttribute05;

    /** フリー属性６ */
    public String freeAttribute06;

    /** フリー属性７ */
    public String freeAttribute07;

    /** フリー属性８ */
    public String freeAttribute08;

    /** フリー属性９ */
    public String freeAttribute09;

    /** フリー属性１０ */
    public String freeAttribute10;

    /** 選択不可フラグ(プルダウン表示対象にするかどうか) */
    public String selFlg;

    /** 変更可能フラグ */
    public String allowUpdateFlg;

}
