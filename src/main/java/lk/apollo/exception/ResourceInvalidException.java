package lk.apollo.exception;

import org.springframework.http.HttpStatus;

public class ResourceInvalidException extends BaseException {

    public ResourceInvalidException(String message) {
        super(HttpStatus.BAD_REQUEST, new ErrorResponse(message));
    }
}
