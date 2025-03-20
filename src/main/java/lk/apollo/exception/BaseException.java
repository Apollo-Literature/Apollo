package lk.apollo.exception;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * Base exception class for all custom exceptions
 */
public class BaseException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L; // Serial version UID for serialization
    private HttpStatus httpStatus;
    private ErrorResponse errorResponse;

    /**
     * Constructor for BaseException
     * @param httpStatus - Receives the http status from the child class
     * @param errorResponse - Receives the error response from the child class
     */
    public BaseException(HttpStatus httpStatus, ErrorResponse errorResponse) {
        this.httpStatus = httpStatus;
        this.errorResponse = errorResponse;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public ErrorResponse getResponse() {
        return errorResponse;
    }

    public void setResponse(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }
}
