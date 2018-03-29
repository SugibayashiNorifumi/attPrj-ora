package application.form;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

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
    private Integer registUserId;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss.SSS")
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss.SSS")
    private LocalDateTime registDate;
    private String registFuncCd;
}
