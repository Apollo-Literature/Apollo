package lk.apollo.Exception;

import org.springframework.http.HttpStatus;

public class BookNotFoundException extends BaseException {
    public BookNotFoundException() {
        super(HttpStatus.NOT_FOUND, new ErrorResponse("Book not found"));
    }
}
