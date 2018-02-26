package application.entity;

/**
 * ユーザマスタエンティティ
 */
public class MUser extends AbstractMasterEntity {
	public Integer userId;
	public String password;
	public String name;
	public String mail;
	public String authCd;
	public String orgCd;
	public Integer managerId;
	public String lineId;
}
