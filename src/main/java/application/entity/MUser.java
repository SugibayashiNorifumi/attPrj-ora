package application.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ユーザマスタエンティティ
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MUser extends AbstractEntity {

    /** ユーザID */
    public Integer userId;

    /** パスワード */
    public String password;

    /** ユーザ氏名 */
    public String name;

    /** メールアドレス(LINEアカウントと紐づけるメールアドレス ) */
    public String mail;

    /** 権限コード(汎用区分:1を参照) */
    public String authCd;

    /** 組織コード(組織マスタ.組織コードのFK) */
    public String orgCd;

    /** 上司ID */
    public Integer managerId;

    /** LINE識別子(LINEアカウントと紐づけた識別子) */
    public String lineId;

}
