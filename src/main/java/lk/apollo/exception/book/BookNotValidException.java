package lk.apollo.exception.book;

import lk.apollo.exception.BaseException;
import lk.apollo.exception.ErrorResponse;
import org.springframework.http.HttpStatus;

public class BookNotValidException extends BaseException {
    public BookNotValidException(String message) {
        super(HttpStatus.BAD_REQUEST, new ErrorResponse(message));
    }
}
