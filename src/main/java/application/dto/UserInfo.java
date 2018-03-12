package application.dto;

import java.io.Serializable;

/**
 * ユーザ情報DTO
 *
 * @author 作成者氏名
 *
 */
public class UserInfo implements Serializable {

    /** ユーザID */
    public String userId;

    /** ユーザ氏名 */
    public String name;

    /** メールアドレス */
    public String mail;

    /** 権限名 */
    public String authName;

    /** 組織名 */
    public String orgName;
}
