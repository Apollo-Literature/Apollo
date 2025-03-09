package lk.apollo.exception.book;

import lk.apollo.exception.BaseException;
import lk.apollo.exception.ErrorResponse;
import org.springframework.http.HttpStatus;

public class BookNotFoundException extends BaseException {
    public BookNotFoundException() {
        super(HttpStatus.NOT_FOUND, new ErrorResponse("Book not found"));
    }
}
