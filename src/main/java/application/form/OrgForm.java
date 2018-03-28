package application.form;

import java.io.Serializable;

import lombok.Data;

/**
 * 組織情報登録フォーム
 */
@Data
public class OrgForm implements Serializable {
    private String orgCd;
    private String orgName;
    private String location;
    private Integer dispSeq;
}
