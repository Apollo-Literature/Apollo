package lk.apollo.exception;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends BaseException {

    public ResourceAlreadyExistsException(String message) {
        super(HttpStatus.BAD_REQUEST, new ErrorResponse(message));
    }
}
