package application.exception;

/**
 * API共通例外クラス
 */
public class APIException extends ApplicationException {

    public APIException(HttpErrors error) {
        super(error);
    }

    public APIException(HttpErrors error, Throwable cause) {
        super(error, cause);
    }

    public APIException(HttpErrors error, String... args) {
        super(error, args);
    }

    public APIException(HttpErrors error, Throwable cause, String... args) {
        super(error, cause, args);
    }
}
