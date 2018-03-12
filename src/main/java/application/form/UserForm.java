package application.form;

import lombok.Data;

/**
 * ユーザ情報登録フォーム
 */
@Data
public class UserForm {
    public Integer userId;
    public String password;
    public String name;
    public String mail;
    public String orgCd;
    public Integer managerId;
    public String authCd;

}

