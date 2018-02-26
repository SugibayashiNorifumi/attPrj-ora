package application.dto;

import java.io.Serializable;

/**
 * ユーザ情報DTO
 */
public class UserInfo implements Serializable {
    public String userId;
    public String name;
    public String mail;
    public String authName;

    public String orgName;
}
