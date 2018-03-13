package application.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <pre>
 * 共通エンティティ.
 * 全テーブルで共通のWhoカラムを管理します
 * </pre>
 * @author 作成者氏名
 *
 */
@Data
public class AbstractEntity implements Serializable {
    /** 登録日時 */
    public LocalDateTime registDate;

    /** 登録者コード */
    public Integer registUserId;

    /** 登録プログラムコード */
    public String registFuncCd;

    /** 更新日時 */
    public LocalDateTime updateDate;

    /** 更新者コード */
    public Integer updateUserId;

    /** 更新プログラムコード */
    public String updateFuncCd;

    /** 論理削除フラグ */
    public String delFlg;
}
