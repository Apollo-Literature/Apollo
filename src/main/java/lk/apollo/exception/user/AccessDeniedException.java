package lk.apollo.exception.user;

import lk.apollo.exception.BaseException;
import lk.apollo.exception.ErrorResponse;
import org.springframework.http.HttpStatus;

public class AccessDeniedException extends BaseException {

    public AccessDeniedException(String message) {
        super(HttpStatus.FORBIDDEN, new ErrorResponse(message));
    }
}
