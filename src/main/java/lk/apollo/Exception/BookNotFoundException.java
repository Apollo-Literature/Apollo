package lk.apollo.Exception;

import org.springframework.http.HttpStatus;

public class BookNotFoundException extends BaseException {
    public BookNotFoundException() {
        super(HttpStatus.BAD_REQUEST, new Response("Book not found"));
    }
}
