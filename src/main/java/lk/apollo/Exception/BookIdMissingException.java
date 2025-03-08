package lk.apollo.Exception;

import org.springframework.http.HttpStatus;

public class BookIdMissingException extends BaseException {

    public BookIdMissingException(String message) {
        super(HttpStatus.BAD_REQUEST, new ErrorResponse(message));
    }
}
