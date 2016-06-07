package VRAPI.Exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_IMPLEMENTED, reason = "API Is under development")
public class HttpNotImplementedException extends RuntimeException {
    public HttpNotImplementedException(String message) {
        super(message);
    }

    public HttpNotImplementedException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpNotImplementedException(Throwable e) {
        super(e);
    }
}
