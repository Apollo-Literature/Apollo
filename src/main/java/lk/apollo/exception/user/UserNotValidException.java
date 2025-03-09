package lk.apollo.exception.user;

import lk.apollo.exception.BaseException;
import lk.apollo.exception.ErrorResponse;
import org.springframework.http.HttpStatus;

public class UserNotValidException extends BaseException {

    public UserNotValidException(String message) {
        super(HttpStatus.BAD_REQUEST, new ErrorResponse(message));
    }
}
