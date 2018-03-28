package application.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

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
    private LocalDateTime registDate;

    /** 登録者コード */
    private Integer registUserId;

    /** 登録プログラムコード */
    private String registFuncCd;

    /** 更新日時 */
    private LocalDateTime updateDate;

    /** 更新者コード */
    private Integer updateUserId;

    /** 更新プログラムコード */
    private String updateFuncCd;

    /** 論理削除フラグ */
    private String delFlg;
}
