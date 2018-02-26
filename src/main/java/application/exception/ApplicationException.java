package application.exception;

import java.text.MessageFormat;

/**
 * 画面共通例外クラス
 *
 */
public class ApplicationException extends RuntimeException {

    private HttpErrors error;

    private Throwable cause;

    private Object[] args;

    public ApplicationException(HttpErrors error) {
        super();
        this.error = error;
    }

    public ApplicationException(HttpErrors error, Throwable cause) {

        super();
        this.error = error;
        this.cause = cause;
    }

    public ApplicationException(HttpErrors error, String... args) {
        super();
        this.error = error;
        this.args = args;
    }

    public ApplicationException(HttpErrors error, Throwable cause, String... args) {
        super();
        this.error = error;
        this.args = args;
        this.cause = cause;
    }

    public String getMessage() {
        if (args != null) {
            return MessageFormat.format(error.getMessage(), args);
        } else {
            return error.getMessage();
        }
    }

    public HttpErrors getError() {
        return error;
    }

    public Throwable getCause() {
        return cause;
    }

    public Object[] getArgs() {
        return args;
    }
}
