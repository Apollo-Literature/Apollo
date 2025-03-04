package lk.apollo.Exception;

import org.springframework.http.HttpStatus;

public class NoBooksFoundException extends BaseException {
    public NoBooksFoundException() {
        super(HttpStatus.NO_CONTENT, new Response("No books found"));
    }
}
