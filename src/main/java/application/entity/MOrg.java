package application.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 組織マスタエンティティ
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MOrg extends AbstractEntity {

	/** 組織コード */
    public String orgCd;

    /** 組織名 */
    public String orgName;

    /** 拠点 */
    public String location;

    /** 表示順序 */
    public Integer dispSeq;

}
