package VRAPI;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "An error occured on the server, the dev team is working day and night to resolve the issue! Have a cookie and try again tomorrow.")

public class HttpInternalServerError extends RuntimeException {
    public HttpInternalServerError(String message) {
        super(message);
    }

    public HttpInternalServerError(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpInternalServerError(Throwable e) {
        super(e);
    }
}
