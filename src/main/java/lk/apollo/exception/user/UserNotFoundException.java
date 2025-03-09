package lk.apollo.exception.user;

import lk.apollo.exception.BaseException;
import lk.apollo.exception.ErrorResponse;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {

    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, new ErrorResponse("User not found"));
    }
}
