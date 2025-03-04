package lk.apollo.Exception;

import org.springframework.http.HttpStatus;

public class BaseException extends RuntimeException {
    private HttpStatus httpStatus;
    private Response response;

    public BaseException(HttpStatus httpStatus, Response response) {
        this.httpStatus = httpStatus;
        this.response = response;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
