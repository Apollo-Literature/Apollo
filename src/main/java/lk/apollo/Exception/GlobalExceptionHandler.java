package lk.apollo.Exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Response> handleBaseException(BaseException exception) {
        return ResponseEntity.status(exception.getHttpStatus()).body(exception.getResponse());
    }
}
