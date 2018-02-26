package application.exception;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import application.controller.WebController;

/**
 * 画面コントローラ共通例外処理クラス
 *
 */
@ControllerAdvice(basePackageClasses = WebController.class)
public class ControllerExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(ApplicationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleApplicationException(ApplicationException ex) {
        return handleError(ex.getError(), ex, ex.getArgs());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleRuntimeException(RuntimeException ex) {
        return handleError(ApplicationErrors.UNEXPECTED, ex, ex.toString());
    }

    private ModelAndView handleError(HttpErrors error, Exception ex, Object... args) {
        String message;
        if (args != null) {
            message = MessageFormat.format(error.getMessage(), args);
        } else {
            message = error.getMessage();
        }

        logger.error(message, ex);

        return new ModelAndView("error/error")
            .addObject("errorMessage", message);
    }
}
