package application.form;

import java.io.Serializable;

import lombok.Data;

/**
 * 組織情報登録フォーム
 */
@Data
public class OrgForm implements Serializable {
    public String orgCd;
    public String orgName;
    public String location;
    public Integer dispSeq;
}
