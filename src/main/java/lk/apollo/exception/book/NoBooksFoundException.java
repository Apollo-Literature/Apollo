package lk.apollo.exception.book;

import lk.apollo.exception.BaseException;
import lk.apollo.exception.ErrorResponse;
import org.springframework.http.HttpStatus;

public class NoBooksFoundException extends BaseException {
    public NoBooksFoundException() {
        super(HttpStatus.NO_CONTENT, new ErrorResponse("No books found"));
    }
}
