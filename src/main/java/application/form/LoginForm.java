package application.form;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 管理者ログイン用Formクラス.
 * @author svcn053
 *
 */
public class LoginForm {

    /**
     *
     */
    @NotBlank
    @Email
    public String mail;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

}
