package application.form;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * ユーザ情報登録フォーム
 */
@Data
public class UserForm {
	@NotNull
	@Digits(integer = 6, fraction = 0)
    private Integer userId;
    private String password;
    private String name;
    private String mail;
    private String orgCd;
    private Integer managerId;
    private String authCd;
}
