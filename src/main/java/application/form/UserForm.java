package application.form;

import lombok.Data;

/**
 * ユーザ情報登録フォーム
 */
@Data
public class UserForm {
    private Integer userId;
    private String password;
    private String name;
    private String mail;
    private String orgCd;
    private Integer managerId;
    private String authCd;
}
