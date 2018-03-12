package application.exception;

import org.springframework.http.HttpStatus;

/**
 * エラー情報インターフェース
 *
 */
public interface HttpErrors {
    /**
     * エラーコードを取得する。
     */
    String getErrorCode();

    /**
     * HTTPスタースを取得する。
     *
     * @return HTTPステータス
     */
    HttpStatus getStatus();

    /**
     * エラーメッセージを取得する。
     *
     * @return エラーメッセージ
     */
    String getMessage();

}
