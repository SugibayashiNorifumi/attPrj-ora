package application.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * マスター共通エンティティ
 */
public class AbstractMasterEntity implements Serializable {
    public LocalDateTime registDate;
    public Integer registUserId;
    public String registFuncCd;
    public LocalDateTime updateDate;
    public Integer updateUserId;
    public String updateFuncCd;
    public String delFlg;
}
