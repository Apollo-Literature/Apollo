package lk.apollo.Exception;

import org.springframework.http.HttpStatus;

public class BookNotValidException extends BaseException {
    public BookNotValidException(String message) {
        super(HttpStatus.BAD_REQUEST, new Response(message));
    }
}
